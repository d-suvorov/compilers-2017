package org.wotopul

import org.wotopul.AbstractNode.Program
import org.wotopul.AbstractNode.Program.*
import org.wotopul.Configuration.OutputItem

fun interpret(program: Program, input: List<Int>): List<OutputItem> =
    eval(program, Configuration(input)).output

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

fun eval(program: Program, start: Configuration): Configuration = when (program) {
    is Skip -> start

    is Assignment -> {
        val value = eval(program.value, start.environment)
        val name = program.variable
        val updated = start.environment + (name to value)
        Configuration(start.input, start.output, updated)
    }

    is Read -> {
        if (start.input.isEmpty())
            throw ExecutionException("input is empty")
        val inputHead = start.input.first()
        val inputTail = start.input.subList(1, start.input.size)
        val name = program.variable
        val updated = start.environment + (name to inputHead)
        Configuration(inputTail, start.output + OutputItem.Prompt, updated)
    }

    is Write -> {
        val value = eval(program.value, start.environment)
        Configuration(start.input, start.output + OutputItem.Number(value), start.environment)
    }

    is Sequence -> evalSequentially(program.first, program.rest, start)

    is If -> {
        val condValue = eval(program.condition, start.environment).toBoolean()
        val clause = if (condValue) program.thenClause else program.elseClause
        eval(clause, start)
    }

    is While -> {
        val condValue = eval(program.condition, start.environment).toBoolean()
        if (condValue) evalSequentially(program.body, program, start)
        else start
    }
}

fun evalSequentially(first: Program, second: Program, start: Configuration) =
    eval(second, eval(first, start))
