package org.wotopul

import org.wotopul.Configuration.OutputItem
import org.wotopul.Statement.*

fun interpret(program: Program, input: List<Int>): List<OutputItem> =
    eval(program.main, Configuration(input, program)).output

open class Configuration(
    open val input: List<Int>,
    open val output: List<OutputItem> = emptyList(),
    open val environment: Map<String, Int> = emptyMap(),
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

    fun updateEnvironment(newEnv: Map<String, Int>) =
        Configuration(this.input, this.output, newEnv, this.functions)

    fun returned() = returnValueVarName in environment

    fun returnValue() = environment[returnValueVarName]
        ?: throw ExecutionException("return statement expected")

    fun updateReturnValue(value: Int) =
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
            val updated = start.environment + (name to inputHead)
            Configuration(inputTail, start.output + OutputItem.Prompt, updated, start.functions)
        }

        is Write -> {
            val (afterExpr, value) = eval(stmt.value, start)
            val updatedOutput = afterExpr.output + OutputItem.Number(value)
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

fun evalFunction(function: FunctionCall, conf: Configuration): Pair<Configuration, Int> {
    val definition = conf.functions[function.name]
        ?: throw ExecutionException("undefined function: ${function.name}")
    if (definition.params.size != function.args.size) {
        throw ExecutionException("cannot apply function ${function.name}" +
            " to ${function.args.size} arguments")
    }
    var curr: Configuration = conf
    val argsEnv = HashMap<String, Int>()
    for (i in function.args.indices) {
        val paramName = definition.params[i]
        val (next, paramValue) = eval(function.args[i], curr)
        argsEnv.put(paramName, paramValue)
        curr = next
    }
    val local = curr.updateEnvironment(argsEnv)
    val after = eval(definition.body, local)
    val returnValue = after.returnValue()
    return Pair(after.updateEnvironment(conf.environment), returnValue)
}
