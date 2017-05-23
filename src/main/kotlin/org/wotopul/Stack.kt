package org.wotopul

import org.wotopul.Configuration.OutputItem
import org.wotopul.Configuration.OutputItem.Number
import org.wotopul.Configuration.OutputItem.Prompt
import org.wotopul.Primitive.*
import org.wotopul.StackOp.*
import java.util.*

sealed class StackOp {
    object Nop : StackOp()

    object Read : StackOp()
    object Write : StackOp()

    object Pop : StackOp()
    class Push(val value: Primitive) : StackOp()
    class Load(val name: String) : StackOp()
    class Store(val name: String) : StackOp()

    class Binop(val op: String) : StackOp()

    class Label(val name: String) : StackOp()
    class Jump(val label: String) : StackOp()
    class Jnz(val label: String) : StackOp()

    class Call(val name: String) : StackOp()
    class Enter(val name: String, val params: List<String>) : StackOp()
    object Return : StackOp()
}

fun compile(program: Program): List<StackOp> {
    var labelCounter = 0

    fun nextLabel(): String {
        val res = "_label$labelCounter"
        labelCounter++
        return res
    }

    fun compile(stmt: Statement): List<StackOp> = when (stmt) {
        is Statement.Skip -> listOf(Nop)
        is Statement.Sequence -> compile(stmt.first) + compile(stmt.rest)

        is Statement.Assignment -> compile(stmt.value) + Store(stmt.variable)
        is Statement.Read -> listOf(Read, Store(stmt.variable))
        is Statement.Write -> compile(stmt.value) + Write

        is Statement.If -> {
            val thenLabel = Label(nextLabel())
            val fiLabel = Label(nextLabel())
            compile(stmt.condition) + Jnz(thenLabel.name) +
                compile(stmt.elseClause) + Jump(fiLabel.name) +
                thenLabel + compile(stmt.thenClause) + fiLabel
        }

        is Statement.While -> {
            val condLabel = Label(nextLabel())
            val bodyLabel = Label(nextLabel())
            val odLabel = Label(nextLabel())
            listOf(condLabel) + compile(stmt.condition) +
                Jnz(bodyLabel.name) + Jump(odLabel.name) +
                bodyLabel + compile(stmt.body) +
                Jump(condLabel.name) + odLabel
        }

        is Statement.Repeat -> {
            val beginLabel = Label(nextLabel())
            val endLabel = Label(nextLabel())
            listOf(beginLabel) + compile(stmt.body) + compile(stmt.condition) +
                Jnz(endLabel.name) + Jump(beginLabel.name) + endLabel
        }

        is Statement.Return -> compile(stmt.value) + Return

        is Statement.FunctionStatement -> compile(stmt.function) + Pop
    }


    fun compile(function: FunctionDefinition) =
        listOf(
            Label(function.name),
            Enter(function.name, function.params)
        ) + compile(function.body)

    val result = mutableListOf<StackOp>()
    for (function in program.functions) {
        result += compile(function)
    }
    return result + Label(mainLabel) + compile(program.main)
}

fun compile(expr: Expr): List<StackOp> = when (expr) {
    is Expr.Const -> listOf(Push(IntT(expr.value)))
    is Expr.Variable -> listOf(Load(expr.name))
    is Expr.Binop -> compile(expr.lhs) + compile(expr.rhs) + Binop(expr.op)
    is Expr.FunctionExpr -> compile(expr.function)

    is Expr.CharLiteral -> listOf(Push(CharT(expr.value)))
    is Expr.StringLiteral -> listOf(Push(StringT(expr.value.toCharArray())))
}

fun compile(function: FunctionCall): List<StackOp> {
    val result = mutableListOf<StackOp>()
    for (expr in function.args.reversed()) {
        result += compile(expr)
    }
    return result + Call(function.name)
}

class StackConf(
    override var input: List<Int>,
    override var output: List<OutputItem> = emptyList(),
    var stack: List<Primitive> = emptyList(),
    val frames: MutableList<MutableMap<String, Primitive>> = mutableListOf(mutableMapOf())
)
    : Configuration(input, output, emptyMap())
{
    override val environment: MutableMap<String, Primitive>
        get() = frames.last()

    fun enter() {
        frames += mutableMapOf()
    }

    fun ret() {
        frames.removeAt(frames.lastIndex)
    }
}

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

    fun popOrThrow(): Primitive {
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
                curr.stack += IntT(inputHead)
            }

            is Write -> curr.output += Number(popOrThrow().toInt())

            is Pop -> popOrThrow()

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
                curr.stack += evalBinary(op.op, lhs, rhs)
            }

            is Label -> {}

            is Jump -> next = labelIndex(op.label)

            is Jnz -> {
                val labelIdx = labelIndex(op.label)
                if (popOrThrow().toInt() != 0) next = labelIdx
            }

            is Call -> {
                curr.stack += IntT(ip) // treat instruction pointer as an integer
                next = labelIndex(op.name)
            }

            is Enter -> {
                val returnAddress = popOrThrow()
                curr.enter()
                for (param in op.params) {
                    curr.environment += (param to popOrThrow())
                }
                curr.stack += returnAddress
            }

            is Return -> {
                val returnValue = popOrThrow()
                next = popOrThrow().toInt()
                curr.stack += returnValue
                curr.ret()
            }
        }
        run(++next)
    }

    run(labelIndex(mainLabel))

    return curr
}