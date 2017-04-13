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
            val value = eval(stmt.value, start)
            val name = stmt.variable
            val updated = start.environment + (name to value)
            Configuration(start.input, start.output, updated, start.functions)
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
            val value = eval(stmt.value, start)
            val updatedOutput = start.output + OutputItem.Number(value)
            Configuration(start.input, updatedOutput, start.environment, start.functions)
        }

        is Sequence -> evalSequentially(stmt.first, stmt.rest, start)

        is If -> {
            val condValue = eval(stmt.condition, start).toBoolean()
            val clause = if (condValue) stmt.thenClause else stmt.elseClause
            eval(clause, start)
        }

        is While -> {
            val condValue = eval(stmt.condition, start).toBoolean()
            if (condValue) evalSequentially(stmt.body, stmt, start)
            else start
        }

        is Repeat -> {
            val afterFirst = eval(stmt.body, start)
            val condValue = eval(stmt.condition, afterFirst).toBoolean()
            if (!condValue) eval(stmt, afterFirst) else afterFirst
        }

        is Return -> start.updateReturnValue(eval(stmt.value, start))

        is FunctionStatement -> evalFunction(stmt.function, start)
    }

fun evalSequentially(first: Statement, second: Statement, start: Configuration) =
    eval(second, eval(first, start))

fun evalFunction(function: FunctionCall, conf: Configuration): Configuration {
    val definition = conf.functions[function.name]
        ?: throw ExecutionException("undefined function: ${function.name}")
    if (definition.params.size != function.args.size) {
        throw ExecutionException("cannot apply function ${function.name}" +
            " to ${function.args.size} arguments")
    }
    val argsEnv = HashMap<String, Int>()
    for (i in function.args.indices) {
        val paramName = definition.params[i]
        val paramValue = eval(function.args[i], conf)
        argsEnv.put(paramName, paramValue)
    }
    val local = conf.updateEnvironment(argsEnv)
    return eval(definition.body, local).updateEnvironment(conf.environment)
}
