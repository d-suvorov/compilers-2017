package org.wotopul

import org.wotopul.Configuration.OutputItem
import org.wotopul.Primitive.IntT
import org.wotopul.Primitive.StringT
import org.wotopul.Statement.*

sealed class Primitive {
    class IntT(val value: Int) : Primitive()
    class CharT(val value: Char) : Primitive()
    class StringT(val value: String) : Primitive()

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

    companion object {
        fun of(value: Any) : Primitive = when (value) {
            is Int -> IntT(value)
            is Char -> CharT(value)
            is String -> StringT(value)
            else -> throw AssertionError()
        }
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

fun evalFunction(function: FunctionCall, conf: Configuration): Pair<Configuration, Primitive> {
    if (function.name == "strlen")
        return strlen(function, conf)
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
    return Pair(after.updateEnvironment(conf.environment), returnValue)
}

private fun strlen(function: FunctionCall, conf: Configuration): Pair<Configuration, IntT> {
    checkArgsSize(1, function)
    val (after, arg) = eval(function.args.first(), conf)
    if (arg !is StringT)
        throw ExecutionException("strlen can only be applied to a string")
    return Pair(after, IntT(arg.value.length))
}

fun checkArgsSize(paramsSize: Int, function: FunctionCall) {
    if (paramsSize != function.args.size) {
        throw ExecutionException("cannot apply function ${function.name}" +
            " to ${function.args.size} arguments")
    }
}