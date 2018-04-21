package org.wotopul

import org.wotopul.Configuration.OutputItem
import org.wotopul.Statement.*
import org.wotopul.VarValue.*

interface ReferenceT

sealed class VarValue {
    class IntT(val value: Int) : VarValue() {
        override fun toString(): String {
            return "Int($value)"
        }
    }

    class CharT(val value: Char) : VarValue() {
        override fun toString(): String {
            return "Char($value)"
        }
    }

    class StringT(val value: CharArray) : VarValue(), ReferenceT

    class UnboxedArrayT(val value: Array<Int>?) : VarValue(), ReferenceT {
        val size: Int
            get() {
                if (value == null)
                    throw ExecutionException("null pointer")
                return value.size
            }

        fun get(i: Int): Int {
            if (value == null)
                throw ExecutionException("null pointer")
            return value[i]
        }

        fun set(i: Int, v: Int) {
            if (value == null)
                throw ExecutionException("null pointer")
            value[i] = v
        }
    }

    class BoxedArrayT(val value: Array<ReferenceT?>?) : VarValue(), ReferenceT {
        val size: Int
            get() {
                if (value == null)
                    throw ExecutionException("null pointer")
                return value.size
            }

        fun get(i: Int): ReferenceT? {
            if (value == null)
                throw ExecutionException("null pointer")
            return value[i]
        }

        fun set(i: Int, v: ReferenceT?) {
            if (value == null)
                throw ExecutionException("null pointer")
            value[i] = v
        }
    }

    class FunctionPointerT(val name: String) : VarValue()

    fun type(): String = when (this) {
        is IntT -> "int"
        is CharT -> "char"
        is StringT -> "string"
        is UnboxedArrayT -> "unboxed array"
        is BoxedArrayT -> "boxed array"
        is FunctionPointerT -> "function pointer"
    }

    fun toInt(): Int = when (this) {
        is IntT -> value
        is CharT -> value.toInt()
        else -> throw ExecutionException(
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

    fun asUnboxedArrayT(): UnboxedArrayT =
        if (this is UnboxedArrayT) this
        else throw ExecutionException(
            "conversions of ${type()} to unboxed array are not allowed")

    fun asBoxedArrayT(): BoxedArrayT =
        if (this is BoxedArrayT) this
        else throw ExecutionException(
            "conversions of ${type()} to boxed array are not allowed")

    fun asFunctionPointerT(): FunctionPointerT =
        if (this is FunctionPointerT) this
        else throw ExecutionException(
            "conversions of ${type()} to function pointer are not allowed")
}

fun interpret(program: Program, input: List<Int>): List<OutputItem> =
    eval(program.main, Configuration(input, program)).output

open class Configuration(
    open val input: List<Int>,
    open val output: List<OutputItem> = emptyList(),
    open val environment: Map<String, VarValue> = emptyMap(),
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

    fun updateEnvironment(newEnv: Map<String, VarValue>) =
        Configuration(this.input, this.output, newEnv, this.functions)

    fun returned() = returnValueVarName in environment

    fun returnValue() = environment[returnValueVarName]
        ?: throw ExecutionException("return statement expected")

    fun updateReturnValue(value: VarValue) =
        updateEnvironment(environment + (returnValueVarName to value))

    companion object { const val returnValueVarName = "return" }
}

fun eval(stmt: Statement, start: Configuration): Configuration =
    if (start.returned()) start
    else when (stmt) {
        is Skip -> start

        is Assignment -> {
            val (afterCond, rhsValue) = eval(stmt.value, start)
            val variable = stmt.variable
            val name = variable.name
            if (!variable.array) {
                val updated = afterCond.environment + (name to rhsValue)
                Configuration(afterCond.input, afterCond.output, updated, afterCond.functions)
            } else {
                val (lastConf1, lastArray) = evalArray(afterCond, variable, variable.indices.size - 1)
                val (lastConf2, index) = eval(variable.indices.last(), lastConf1)
                when (lastArray) {
                    is UnboxedArrayT -> lastArray.set(index.toInt(), rhsValue.toInt())
                    is BoxedArrayT -> lastArray.set(index.toInt(), rhsValue as ReferenceT)
                    else -> throw ExecutionException("cannot index primitive value")
                }
                lastConf2
            }
        }

        is Read -> {
            if (start.input.isEmpty())
                throw ExecutionException("input is empty")
            val inputHead = start.input.first()
            val inputTail = start.input.subList(1, start.input.size)

            // TODO cut'n'paste
            val variable = stmt.variable
            val name = variable.name
            if (!variable.array) {
                val updated = start.environment + (name to IntT(inputHead))
                Configuration(inputTail, start.output + OutputItem.Prompt, updated, start.functions)
            } else {
                val (lastConf1, lastArray) = evalArray(start, variable, variable.indices.size - 1)
                val (lastConf2, index) = eval(variable.indices.last(), lastConf1)
                when (lastArray) {
                    is UnboxedArrayT -> lastArray.set(index.toInt(), inputHead)
                    is BoxedArrayT -> throw ExecutionException("cannot store primitive value in a reference array")
                    else -> throw ExecutionException("cannot index primitive value")
                }
                Configuration(
                    inputTail,
                    lastConf2.output + OutputItem.Prompt,
                    lastConf2.environment,
                    lastConf2.functions
                )
            }
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

fun evalFunction(function: FunctionCall, conf: Configuration): Pair<Configuration, VarValue> =
    when(function.name) {
        "strlen" -> strlen(function, conf)
        "strget" -> strget(function, conf)
        "strset" -> strset(function, conf)
        "strsub" -> strsub(function, conf)
        "strdup" -> strdup(function, conf)
        "strcat" -> strcat(function, conf)
        "strcmp" -> strcmp(function, conf)
        "strmake" -> strmake(function, conf)

        "arrlen" -> arrlen(function, conf)
        "arrmake" -> arrmake(function, conf)

        "Arrmake" -> Arrmake(function, conf)

        else -> {
            val name = if (conf.functions.containsKey(function.name)) {
                function.name
            } else {
                val symbol = conf.environment[function.name]
                    ?: throw ExecutionException("undefined name: ${function.name}")
                val pointer = symbol.asFunctionPointerT()
                pointer.name
            }

            val definition = conf.functions[name]
                ?: throw ExecutionException("undefined function: ${function.name}")
            checkArgsSize(definition.params.size, function)

            var curr: Configuration = conf
            val argsEnv = HashMap<String, VarValue>()
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

fun arrlen(function: FunctionCall, conf: Configuration): Pair<Configuration, IntT> {
    checkArgsSize(1, function)
    val (after1, array) = eval(function.args[0], conf)
    val res = arrlen(array)
    return Pair(after1, res)
}

fun arrmake(function: FunctionCall, conf: Configuration): Pair<Configuration, UnboxedArrayT> {
    checkArgsSize(2, function)
    val (after1, length) = eval(function.args[0], conf)
    val (after2, value) = eval(function.args[1], after1)
    val res = arrmake(length.asIntT(), value.asIntT())
    return Pair(after2, res)
}

fun Arrmake(function: FunctionCall, conf: Configuration): Pair<Configuration, BoxedArrayT> {
    checkArgsSize(2, function)
    val (after1, length) = eval(function.args[0], conf)
    val (after2, init) = eval(function.args[1], after1)
    val res = Arrmake(length.asIntT(), init.asBoxedArrayT())
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