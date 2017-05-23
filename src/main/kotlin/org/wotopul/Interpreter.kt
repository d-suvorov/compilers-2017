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
    checkArgsSize(1, function)
    val (after, arg) = eval(function.args.first(), conf)
    if (arg !is StringT)
        throw ExecutionException("strlen can not be applied to ${arg.type()}")
    return Pair(after, IntT(arg.value.size))
}

fun strget(function: FunctionCall, conf: Configuration): Pair<Configuration, CharT> {
    checkArgsSize(2, function)
    val (after1, str) = eval(function.args[0], conf)
    val (after2, idx) = eval(function.args[1], after1)
    if (str !is StringT || idx !is IntT) {
        throw ExecutionException(
            "strget can not be applied to ${str.type()} and ${idx.type()}")
    }
    return Pair(after2, CharT(str.value[idx.value]))
}

fun strset(function: FunctionCall, conf: Configuration): Pair<Configuration, IntT> {
    checkArgsSize(3, function)
    val (after1, str) = eval(function.args[0], conf)
    val (after2, idx) = eval(function.args[1], after1)
    val (after3, chr) = eval(function.args[2], after2)
    if (str !is StringT || idx !is IntT || chr !is CharT) {
        throw ExecutionException(
            "strset can not be applied to ${str.type()} and ${idx.type()} and ${chr.type()}")
    }
    str.value[idx.value] = chr.value
    return Pair(after3, IntT(0))
}

fun strsub(function: FunctionCall, conf: Configuration): Pair<Configuration, StringT> {
    checkArgsSize(3, function)
    val (after1, str) = eval(function.args[0], conf)
    val (after2, offset) = eval(function.args[1], after1)
    val (after3, length) = eval(function.args[2], after2)
    if (str !is StringT || offset !is IntT || length !is IntT) {
        throw ExecutionException(
            "strsub can not be applied to ${str.type()} and ${offset.type()} and ${length.type()}")
    }
    val substring = String(str.value, offset.value, length.value)
    return Pair(after3, StringT(substring.toCharArray()))
}

fun strdup(function: FunctionCall, conf: Configuration): Pair<Configuration, StringT> {
    checkArgsSize(1, function)
    val (after, str) = eval(function.args[0], conf)
    if (str !is StringT) {
        throw ExecutionException("strdup can not be applied to ${str.type()}")
    }
    return Pair(after, StringT(str.value.copyOf()))
}

fun strcat(function: FunctionCall, conf: Configuration): Pair<Configuration, StringT> {
    checkArgsSize(2, function)
    val (after1, str1) = eval(function.args[0], conf)
    val (after2, str2) = eval(function.args[1], after1)
    if (str1 !is StringT || str2 !is StringT) {
        throw ExecutionException(
            "strcat can not be applied to ${str1.type()} and ${str2.type()}")
    }
    val concatenated = String(str1.value) + String(str2.value)
    return Pair(after2, StringT(concatenated.toCharArray()))
}

fun strcmp(function: FunctionCall, conf: Configuration): Pair<Configuration, IntT> {
    checkArgsSize(2, function)
    val (after1, str1) = eval(function.args[0], conf)
    val (after2, str2) = eval(function.args[1], after1)
    if (str1 !is StringT || str2 !is StringT) {
        throw ExecutionException(
            "strcmp can not be applied to ${str1.type()} and ${str2.type()}")
    }
    val res = String(str1.value).compareTo(String(str2.value))
    return Pair(after2, IntT(res))
}

fun strmake(function: FunctionCall, conf: Configuration): Pair<Configuration, StringT> {
    checkArgsSize(2, function)
    val (after1, length) = eval(function.args[0], conf)
    val (after2, chr) = eval(function.args[1], after1)
    if (length !is IntT || chr !is CharT) {
        throw ExecutionException(
            "strmake can not be applied to ${length.type()} and ${chr.type()}")
    }
    val str = CharArray(length.value) { chr.value }
    return Pair(after2, StringT(str))
}

fun checkArgsSize(paramsSize: Int, function: FunctionCall) {
    if (paramsSize != function.args.size) {
        throw ExecutionException("cannot apply function ${function.name}" +
            " to ${function.args.size} arguments")
    }
}