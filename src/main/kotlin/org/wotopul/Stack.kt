package org.wotopul

import org.wotopul.AbstractNode.Expr
import org.wotopul.AbstractNode.Program
import org.wotopul.Configuration.OutputItem
import org.wotopul.Configuration.OutputItem.Number
import org.wotopul.Configuration.OutputItem.Prompt
import org.wotopul.StackOp.*
import java.util.*

sealed class StackOp {
    object Nop : StackOp()
    object Read : StackOp()
    object Write : StackOp()
    class Push(val value: Int) : StackOp()
    class Load(val name: String) : StackOp()
    class Store(val name: String) : StackOp()
    class Binop(val op: String) : StackOp()
    class Label(val name: String) : StackOp()
    class Jump(val label: String) : StackOp()
    class Jnz(val label: String) : StackOp()
}

fun compile(expr: Expr): List<StackOp> = when (expr) {
    is Expr.Const -> listOf(Push(expr.value))
    is Expr.Variable -> listOf(Load(expr.name))
    is Expr.Binop -> compile(expr.lhs) + compile(expr.rhs) + Binop(expr.op)
}

fun compile(program: Program): List<StackOp> {
    var labelCounter = 0

    fun nextLabel(): String {
        val res = "label$labelCounter"
        labelCounter++
        return res
    }

    fun compileImpl(program: Program): List<StackOp> = when (program) {
        is Program.Skip -> listOf(Nop)
        is Program.Sequence -> compileImpl(program.first) + compileImpl(program.rest)

        is Program.Assignment -> compile(program.value) + Store(program.variable)
        is Program.Read -> listOf(Read, Store(program.variable))
        is Program.Write -> compile(program.value) + Write

        is Program.If -> {
            val thenLabel = Label(nextLabel())
            val fiLabel = Label(nextLabel())
            compile(program.condition) + Jnz(thenLabel.name) +
                compileImpl(program.elseClause) + Jump(fiLabel.name) +
                thenLabel + compileImpl(program.thenClause) + fiLabel
        }

        is Program.While -> {
            val condLabel = Label(nextLabel())
            val bodyLabel = Label(nextLabel())
            val odLabel = Label(nextLabel())
            listOf(condLabel) + compile(program.condition) +
                Jnz(bodyLabel.name) + Jump(odLabel.name) +
                bodyLabel + compileImpl(program.body) +
                Jump(condLabel.name) + odLabel
        }

        is Program.Repeat -> {
            val beginLabel = Label(nextLabel())
            val endLabel = Label(nextLabel())
            listOf(beginLabel) + compileImpl(program.body) + compile(program.condition) +
                Jnz(endLabel.name) + Jump(beginLabel.name) + endLabel
        }
    }

    return compileImpl(program)
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

    val labelTable = HashMap<String, Int>()
    for (i in program.indices) {
        val op = program[i]
        if (op is Label) {
            if (labelTable.containsKey(op.name))
                throw ExecutionException("duplicate label: ${op.name}")
            labelTable[op.name] = i
        }
    }

    fun labelIndex(label: String) = labelTable[label]
        ?: throw ExecutionException("undefined label: $label")

    fun popOrThrow(): Int {
        if (curr.stack.isEmpty())
            throw ExecutionException("empty stack")
        val top = curr.stack.last()
        curr.stack = curr.stack.subList(0, curr.stack.size - 1)
        return top
    }

    tailrec fun run(ip: Int) {
        if (ip == program.size) // terminate, it works for empty programs too
            return
        val op = program[ip]
        var next = ip
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

            is Label -> {}

            is Jump -> next = labelIndex(op.label)

            is Jnz -> {
                val labelIdx = labelIndex(op.label)
                if (popOrThrow() != 0) next = labelIdx
            }
        }
        run(++next)
    }

    run(0)

    return curr
}