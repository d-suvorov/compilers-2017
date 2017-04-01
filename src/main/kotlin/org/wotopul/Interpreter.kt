package org.wotopul

import org.wotopul.Configuration.OutputItem
import org.wotopul.Statement.*

fun interpret(program: Program, input: List<Int>): List<OutputItem> =
    eval(program.main, Configuration(input)).output

open class Configuration(
    open val input: List<Int>,
    open val output: List<OutputItem> = emptyList(),
    open val environment: Map<String, Int> = emptyMap())
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
}

fun eval(statement: Statement, start: Configuration): Configuration = when (statement) {
    is Skip -> start

    is Assignment -> {
        val value = eval(statement.value, start.environment)
        val name = statement.variable
        val updated = start.environment + (name to value)
        Configuration(start.input, start.output, updated)
    }

    is Read -> {
        if (start.input.isEmpty())
            throw ExecutionException("input is empty")
        val inputHead = start.input.first()
        val inputTail = start.input.subList(1, start.input.size)
        val name = statement.variable
        val updated = start.environment + (name to inputHead)
        Configuration(inputTail, start.output + OutputItem.Prompt, updated)
    }

    is Write -> {
        val value = eval(statement.value, start.environment)
        Configuration(start.input, start.output + OutputItem.Number(value), start.environment)
    }

    is Sequence -> evalSequentially(statement.first, statement.rest, start)

    is If -> {
        val condValue = eval(statement.condition, start.environment).toBoolean()
        val clause = if (condValue) statement.thenClause else statement.elseClause
        eval(clause, start)
    }

    is While -> {
        val condValue = eval(statement.condition, start.environment).toBoolean()
        if (condValue) evalSequentially(statement.body, statement, start)
        else start
    }

    is Repeat -> {
        val afterFirst = eval(statement.body, start)
        val condValue = eval(statement.condition, afterFirst.environment).toBoolean()
        if (!condValue) eval(statement, afterFirst) else afterFirst
    }

    is FunctionStatement -> TODO("unimplemented")
}

fun evalSequentially(first: Statement, second: Statement, start: Configuration) =
    eval(second, eval(first, start))
