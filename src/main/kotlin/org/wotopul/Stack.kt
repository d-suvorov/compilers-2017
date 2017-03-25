package org.wotopul

import org.wotopul.AbstractNode.Expr
import org.wotopul.AbstractNode.Program
import org.wotopul.Configuration.OutputItem
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

fun compile(expr: Expr): List<StackOp> = when (expr) {
    is Expr.Const -> listOf(Push(expr.value))
    is Expr.Variable -> listOf(Load(expr.name))
    is Expr.Binop -> compile(expr.lhs) + compile(expr.rhs) + Binop(expr.op)
}

fun compile(program: Program): List<StackOp> = when (program) {
    is Program.Skip -> listOf(Nop)
    is Program.Sequence -> compile(program.first) + compile(program.rest)
    is Program.Assignment -> compile(program.value) + Store(program.variable)
    is Program.Read -> listOf(Read, Store(program.variable))
    is Program.Write -> compile(program.value) + Write
    is Program.If -> TODO("not implemented yet")
}

class StackConf(
    override var input: List<Int>,
    override var output: List<OutputItem> = emptyList(),
    override var environment: Map<String, Int> = emptyMap(),
    var stack: List<Int> = emptyList()
) : Configuration(input, output, environment)

fun interpret(program: List<StackOp>, input: List<Int>): List<OutputItem> =
    interpret(program, StackConf(input)).output

fun interpret(program: List<StackOp>, start: StackConf): StackConf {
    val curr: StackConf = start

    fun popOrThrow(): Int {
        if (curr.stack.isEmpty())
            throw ExecutionException("empty stack")
        val top = curr.stack.last()
        curr.stack = curr.stack.subList(0, curr.stack.size - 1)
        return top
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

            is Write -> curr.output += Number(popOrThrow())

            is Push -> curr.stack += op.value

            is Load -> {
                val value = curr.environment[op.name]
                    ?: throw ExecutionException("undefined variable: ${op.name}")
                curr.stack += value
            }

            is Store -> curr.environment += (op.name to popOrThrow())

            is Binop -> {
                val rhs = popOrThrow()
                val lhs = popOrThrow()
                curr.stack += functionByOperation(op.op) (lhs, rhs)
            }
        }
    }

    program.forEach(::step)
    return curr
}