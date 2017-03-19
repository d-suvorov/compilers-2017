package org.wotopul

import org.wotopul.Configuration.OutputItem.Number
import org.wotopul.Configuration.OutputItem.Prompt
import org.wotopul.StackOp.*

sealed class StackOp {
    object Nop : StackOp()
    object Read : StackOp()
    object Write : StackOp()
    class Push(val value: Int) : StackOp()
    class Load(val name: String) : StackOp()
    class Store(val name: String) : StackOp()
    class Binop(val op: String) : StackOp()
}

class StackConf(
    override var input: List<Int>,
    override var output: List<OutputItem> = emptyList(),
    override var environment: Map<String, Int> = emptyMap(),
    var stack: List<Int> = emptyList()
) : Configuration(input, output, environment)

fun interpret(program: List<StackOp>, start: StackConf): StackConf {
    val curr: StackConf = start

    fun topOrThrow(): Int {
        if (curr.stack.isEmpty())
            throw ExecutionException("empty stack")
        return curr.stack.last()
    }

    fun pop() {
        curr.stack = curr.stack.subList(1, curr.stack.size)
    }

    fun step(op: StackOp) {
        when (op) {
            is Nop -> {}

            is Read -> {
                if (curr.input.isEmpty())
                    throw ExecutionException("input is empty")
                val inputHead = start.input.first()
                val inputTail = start.input.subList(1, start.input.size)
                curr.input = inputTail
                curr.output += Prompt
                curr.stack += inputHead
            }

            is Write -> {
                curr.output += Number(topOrThrow())
                pop()
            }

            is Push -> curr.stack += op.value

            is Load -> {
                val value = curr.environment[op.name]
                    ?: throw ExecutionException("unassigned variable: ${op.name}")
                curr.stack += value
            }

            is Store -> {
                curr.environment += (op.name to topOrThrow())
                pop()
            }

            is Binop -> {
                val lhs = topOrThrow()
                val rhs = topOrThrow()
                curr.stack += functionByOperation(op.op) (lhs, rhs)
            }
        }
    }

    program.forEach(::step)
    return curr
}