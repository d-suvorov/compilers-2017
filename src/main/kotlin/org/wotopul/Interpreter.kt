package org.wotopul

import org.wotopul.Configuration.OutputItem
import org.wotopul.Primitive.*
import org.wotopul.Statement.*

sealed class Primitive {
    class IntT(val value: Int) : Primitive()
    class CharT(val value: Char) : Primitive()
    class StringT(val value: CharArray) : Primitive()

    fun type(): String = when (this) {
        is IntT -> "int"
        is CharT -> "char"
        is StringT -> "string"
    }

    fun toInt(): Int = when (this) {
        is IntT -> value
        is CharT -> value.toInt()
        is StringT -> throw ExecutionException(
            "conversions of ${type()} to int are not allowed")
    }

    fun toBoolean(): Boolean = when (this) {
        is IntT -> value.toBoolean()
        else -> throw ExecutionException(
            "conversions of ${type()} to boolean are not allowed")
    }

    fun asIntT(): IntT =
        if (this is IntT) this
        else throw ExecutionException(
            "conversions of ${type()} to int are not allowed")

    fun asCharT(): CharT =
        if (this is CharT) this
        else throw ExecutionException(
            "conversions of ${type()} to char are not allowed")

    fun asStringT(): StringT =
        if (this is StringT) this
        else throw ExecutionException(
            "conversions of ${type()} to string are not allowed")
}

fun interpret(program: Program, input: List<Int>): List<OutputItem> =
    eval(program.main, Configuration(input, program)).output

open class Configuration(
    open val input: List<Int>,
    open val output: List<OutputItem> = emptyList(),
    open val environment: Map<String, Primitive> = emptyMap(),
    val functions: Map<String, FunctionDefinition> = emptyMap())
{
    sealed class OutputItem {
        object Prompt : OutputItem()
        class Number(val value: Int) : OutputItem() {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other?.javaClass != javaClass) return false
                return value == (other as Number).value
            }

            override fun hashCode(): Int {
                return value
            }
        }

        override fun toString() = when (this) {
            is Prompt -> "> "
            is Number -> "$value\n"
        }
    }

    constructor(input: List<Int>, program: Program)
        : this(input, functions = program.functions.associateBy({ it.name }))

    fun updateEnvironment(newEnv: Map<String, Primitive>) =
        Configuration(this.input, this.output, newEnv, this.functions)

    fun returned() = returnValueVarName in environment

    fun returnValue() = environment[returnValueVarName]
        ?: throw ExecutionException("return statement expected")

    fun updateReturnValue(value: Primitive) =
        updateEnvironment(environment + (returnValueVarName to value))

    companion object { val returnValueVarName = "return" }
}

fun eval(stmt: Statement, start: Configuration): Configuration =
    if (start.returned()) start
    else when (stmt) {
        is Skip -> start

        is Assignment -> {
            val (afterCond, value) = eval(stmt.value, start)
            val name = stmt.variable
            val updated = afterCond.environment + (name to value)
            Configuration(afterCond.input, afterCond.output, updated, afterCond.functions)
        }

        is Read -> {
            if (start.input.isEmpty())
                throw ExecutionException("input is empty")
            val inputHead = start.input.first()
            val inputTail = start.input.subList(1, start.input.size)
            val name = stmt.variable
            val updated = start.environment + (name to IntT(inputHead))
            Configuration(inputTail, start.output + OutputItem.Prompt, updated, start.functions)
        }

        is Write -> {
            val (afterExpr, value) = eval(stmt.value, start)
            val updatedOutput = afterExpr.output + OutputItem.Number(value.toInt())
            Configuration(afterExpr.input, updatedOutput,
                afterExpr.environment, afterExpr.functions)
        }

        is Sequence -> evalSequentially(stmt.first, stmt.rest, start)

        is If -> {
            val (afterCond, condValue) = eval(stmt.condition, start)
            val clause = if (condValue.toBoolean()) stmt.thenClause else stmt.elseClause
            eval(clause, afterCond)
        }

        is While -> {
            val (afterCond, condValue) = eval(stmt.condition, start)
            if (condValue.toBoolean()) evalSequentially(stmt.body, stmt, afterCond)
            else afterCond
        }

        is Repeat -> {
            val afterFirst = eval(stmt.body, start)
            val (afterCond, condValue) = eval(stmt.condition, afterFirst)
            if (!condValue.toBoolean()) eval(stmt, afterCond) else afterCond
        }

        is Return -> {
            val (afterExpr, value) = eval(stmt.value, start)
            afterExpr.updateReturnValue(value)
        }

        is FunctionStatement -> evalFunction(stmt.function, start).first
    }

fun evalSequentially(first: Statement, second: Statement, start: Configuration) =
    eval(second, eval(first, start))

fun evalFunction(function: FunctionCall, conf: Configuration): Pair<Configuration, Primitive> =
    when(function.name) {
        "strlen" -> strlen(function, conf)
        "strget" -> strget(function, conf)
        "strset" -> strset(function, conf)
        "strsub" -> strsub(function, conf)
        "strdup" -> strdup(function, conf)
        "strcat" -> strcat(function, conf)
        "strcmp" -> strcmp(function, conf)
        "strmake" -> strmake(function, conf)

        else -> {
            val definition = conf.functions[function.name]
                ?: throw ExecutionException("undefined function: ${function.name}")
            checkArgsSize(definition.params.size, function)

            var curr: Configuration = conf
            val argsEnv = HashMap<String, Primitive>()
            for (i in function.args.indices) {
                val paramName = definition.params[i]
                val (next, arg) = eval(function.args[i], curr)
                argsEnv.put(paramName, arg)
                curr = next
            }

            val local = curr.updateEnvironment(argsEnv)
            val after = eval(definition.body, local)
            val returnValue = after.returnValue()
            Pair(after.updateEnvironment(conf.environment), returnValue)
        }
    }

fun strlen(function: FunctionCall, conf: Configuration): Pair<Configuration, IntT> {
    checkArgsSize(function)
    val (after, arg) = eval(function.args.first(), conf)
    val res = strlen(arg.asStringT())
    return Pair(after, res)
}

fun strget(function: FunctionCall, conf: Configuration): Pair<Configuration, CharT> {
    checkArgsSize(function)
    val (after1, str) = eval(function.args[0], conf)
    val (after2, idx) = eval(function.args[1], after1)
    val res = strget(str.asStringT(), idx.asIntT())
    return Pair(after2, res)
}

fun strset(function: FunctionCall, conf: Configuration): Pair<Configuration, IntT> {
    checkArgsSize(function)
    val (after1, str) = eval(function.args[0], conf)
    val (after2, idx) = eval(function.args[1], after1)
    val (after3, chr) = eval(function.args[2], after2)
    val res = strset(str.asStringT(), idx.asIntT(), chr.asCharT())
    return Pair(after3, res)
}

fun strsub(function: FunctionCall, conf: Configuration): Pair<Configuration, StringT> {
    checkArgsSize(function)
    val (after1, str) = eval(function.args[0], conf)
    val (after2, offset) = eval(function.args[1], after1)
    val (after3, length) = eval(function.args[2], after2)
    val res = strsub(str.asStringT(), offset.asIntT(), length.asIntT())
    return Pair(after3, res)
}

fun strdup(function: FunctionCall, conf: Configuration): Pair<Configuration, StringT> {
    checkArgsSize(function)
    val (after, str) = eval(function.args[0], conf)
    val res = strdup(str.asStringT())
    return Pair(after, res)
}

fun strcat(function: FunctionCall, conf: Configuration): Pair<Configuration, StringT> {
    checkArgsSize(function)
    val (after1, str1) = eval(function.args[0], conf)
    val (after2, str2) = eval(function.args[1], after1)
    val res = strcat(str1.asStringT(), str2.asStringT())
    return Pair(after2, res)
}

fun strcmp(function: FunctionCall, conf: Configuration): Pair<Configuration, IntT> {
    checkArgsSize(function)
    val (after1, str1) = eval(function.args[0], conf)
    val (after2, str2) = eval(function.args[1], after1)
    val res = strcmp(str1.asStringT(), str2.asStringT())
    return Pair(after2, res)
}

fun strmake(function: FunctionCall, conf: Configuration): Pair<Configuration, StringT> {
    checkArgsSize(function)
    val (after1, length) = eval(function.args[0], conf)
    val (after2, chr) = eval(function.args[1], after1)
    val res = strmake(length.asIntT(), chr.asCharT())
    return Pair(after2, res)
}

fun checkArgsSize(function: FunctionCall) {
    return checkArgsSize(stringIntrinsicNArgs(function.name), function)
}

fun checkArgsSize(paramsSize: Int, function: FunctionCall) {
    if (paramsSize != function.args.size) {
        throw ExecutionException("cannot apply function ${function.name}" +
            " to ${function.args.size} arguments")
    }
}