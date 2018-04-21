package org.wotopul

import org.wotopul.Configuration.OutputItem
import org.wotopul.Configuration.OutputItem.Number
import org.wotopul.Configuration.OutputItem.Prompt
import org.wotopul.Expr.Variable
import org.wotopul.StackOp.*
import org.wotopul.VarValue.*
import java.util.*

sealed class StackOp {
    object Nop : StackOp()

    object Read : StackOp()
    object Write : StackOp()

    object Pop : StackOp()
    class Push(val value: VarValue) : StackOp()

    class Load(val name: String) : StackOp()
    class Store(val name: String) : StackOp()
    object LoadArr : StackOp()
    object StoreArr : StackOp()

    object MakeUnboxedArray : StackOp()
    object MakeBoxedArray : StackOp()

    class Binop(val op: String) : StackOp()

    class Label(val name: String) : StackOp()
    class Jump(val label: String) : StackOp()
    class Jnz(val label: String) : StackOp()

    class Call(val name: String) : StackOp()
    class Enter(val name: String, val params: List<String>) : StackOp()
    object Return : StackOp()

    override fun toString() = when (this) {
        Nop              -> "Nop"
        Read             -> "Read"
        Write            -> "Write"
        Pop              -> "Pop"
        is Push          -> "Push $value"
        is Load          -> "Load $name"
        is Store         -> "Store $name"
        LoadArr          -> "LoadArr"
        StoreArr         -> "StoreArr"
        MakeUnboxedArray -> "MakeUnboxedArray"
        MakeBoxedArray   -> "MakeBoxedArray"
        is Binop         -> "Binop[$op]"
        is Label         -> "Label $name"
        is Jump          -> "Jump $label"
        is Jnz           -> "Jnz $label"
        is Call          -> "Call $name"
        is Enter         -> "Enter $name"
        Return           -> "Return"
    }
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

        is Statement.Assignment -> {
            val variable = stmt.variable
            if (!variable.array) {
                compile(stmt.value) + Store(variable.name)
            } else {
                val res = mutableListOf<StackOp>()
                for (i in variable.indices.size - 2 downTo 0) {
                    val e = variable.indices[i]
                    res += compile(e)
                }
                res += Load(variable.name)
                for (i in 0 until variable.indices.size - 1) {
                    res += LoadArr
                }
                res += compile(stmt.value)
                res += compile(variable.indices.last())
                res += StoreArr
                res += Pop
                res
            }
        }

        is Statement.Read -> {
            val variable = stmt.variable
            if (!variable.array) {
                listOf(Read, Store(stmt.variable.name))
            } else {
                val res = mutableListOf<StackOp>()
                for (i in variable.indices.size - 2 downTo 0) {
                    val e = variable.indices[i]
                    res += compile(e)
                }
                res += Load(variable.name)
                for (i in 0 until variable.indices.size - 1) {
                    res += LoadArr
                }
                res += Read
                res += compile(variable.indices.last())
                res += StoreArr
                res += Pop
                res
            }
        }

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

    is Variable -> {
        if (!expr.array) {
            listOf(Load(expr.name))
        } else {
            val res = mutableListOf<StackOp>()
            for (idxExpr in expr.indices.reversedArray()) {
                res += compile(idxExpr)
            }
            res += Load(expr.name)
            for (i in 0 until expr.indices.size) {
                res += LoadArr
            }
            res
        }
    }

    is Expr.Binop -> compile(expr.lhs) + compile(expr.rhs) + Binop(expr.op)
    is Expr.FunctionExpr -> compile(expr.function)

    is Expr.CharLiteral -> listOf(Push(CharT(expr.value)))
    is Expr.StringLiteral -> listOf(Push(StringT(expr.value.toCharArray())))

    is Expr.UnboxedArrayInitializer -> {
        val res = mutableListOf<StackOp>()
        res += Push(IntT(expr.exprList.size))
        res += MakeUnboxedArray
        for ((i, e) in expr.exprList.withIndex()) {
            res += compile(e)
            res += Push(IntT(i))
            res += StoreArr
        }
        res
    }

    is Expr.BoxedArrayInitializer -> {
        val res = mutableListOf<StackOp>()
        res += Push(IntT(expr.exprList.size))
        res += MakeBoxedArray
        for ((i, e) in expr.exprList.withIndex()) {
            res += compile(e)
            res += Push(IntT(i))
            res += StoreArr
        }
        res
    }
}

fun compile(function: FunctionCall): List<StackOp> {
    val result = mutableListOf<StackOp>()
    for (expr in function.args.reversed()) {
        result += compile(expr)
    }
    return result + Call(function.name)
}

class StackConf(
    var input: List<Int>,
    var output: List<OutputItem> = emptyList(),
    var stack: List<VarValue?> = emptyList(),
    private val frames: MutableList<MutableMap<String, VarValue?>> = mutableListOf(mutableMapOf())
)
{
    val stackEnvironment: MutableMap<String, VarValue?>
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

    fun popOrThrowNullable(): VarValue? {
        if (curr.stack.isEmpty())
            throw ExecutionException("empty stack")
        val top = curr.stack.last()
        curr.stack = curr.stack.subList(0, curr.stack.size - 1)
        return top
    }

    fun popOrThrow(): VarValue {
        return popOrThrowNullable()!!
    }

    fun topOrThrow(): VarValue {
        if (curr.stack.isEmpty())
            throw ExecutionException("empty stack")
        return curr.stack.last()!!
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

            is Pop -> popOrThrowNullable()

            is Push -> curr.stack += op.value

            is Load -> {
                val value = curr.stackEnvironment[op.name]
                    ?: throw ExecutionException("undefined name: ${op.name}")
                curr.stack += value
            }

            is Store -> curr.stackEnvironment += (op.name to popOrThrow())

            is LoadArr -> {
                val array = popOrThrow()
                val index = popOrThrow()
                val value = when (array) {
                    is UnboxedArrayT -> IntT(array.get(index.asIntT().toInt()))
                    is BoxedArrayT -> array.get(index.asIntT().toInt()) as VarValue?
                    else -> throw AssertionError("LoadArr must be used with array")
                }
                curr.stack += value
            }

            is StoreArr -> {
                val index = popOrThrow()
                val value = popOrThrow()
                val array = topOrThrow()
                when (array) {
                    is UnboxedArrayT -> array.set(index.asIntT().toInt(), value.asIntT().toInt())
                    is BoxedArrayT -> array.set(index.asIntT().toInt(), value as ReferenceT)
                    else -> throw AssertionError("LoadArr must be used with array")
                }
            }

            is MakeUnboxedArray -> {
                val size = popOrThrow().asIntT().toInt()
                val array = Array(size, { 0 })
                curr.stack += UnboxedArrayT(array)
            }

            is MakeBoxedArray -> {
                val size = popOrThrow().asIntT().toInt()
                val array = Array<ReferenceT?>(size, { null })
                curr.stack += BoxedArrayT(array)
            }

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
                when (op.name) {
                    "strlen" -> {
                        val str = popOrThrow()
                        curr.stack += strlen(str.asStringT())
                    }
                    "strget" -> {
                        val str = popOrThrow()
                        val idx = popOrThrow()
                        curr.stack += strget(str.asStringT(), idx.asIntT())
                    }
                    "strset" -> {
                        val str = popOrThrow()
                        val idx = popOrThrow()
                        val chr = popOrThrow()
                        curr.stack += strset(str.asStringT(), idx.asIntT(), chr.asCharT())
                    }
                    "strsub" -> {
                        val str = popOrThrow()
                        val offset = popOrThrow()
                        val length = popOrThrow()
                        curr.stack += strsub(str.asStringT(), offset.asIntT(), length.asIntT())
                    }
                    "strdup" -> {
                        val str = popOrThrow()
                        curr.stack += strdup(str.asStringT())
                    }
                    "strcat" -> {
                        val str1 = popOrThrow()
                        val str2 = popOrThrow()
                        curr.stack += strcat(str1.asStringT(), str2.asStringT())
                    }
                    "strcmp" -> {
                        val str1 = popOrThrow()
                        val str2 = popOrThrow()
                        curr.stack += strcmp(str1.asStringT(), str2.asStringT())
                    }
                    "strmake" -> {
                        val length = popOrThrow()
                        val chr = popOrThrow()
                        curr.stack += strmake(length.asIntT(), chr.asCharT())
                    }

                    "arrlen" -> {
                        val array = popOrThrow()
                        curr.stack += arrlen(array)
                    }
                    "arrmake" -> {
                        val length = popOrThrow()
                        val value = popOrThrow()
                        curr.stack += arrmake(length.asIntT(), value.asIntT())
                    }
                    "Arrmake" -> {
                        val length = popOrThrow()
                        val initializer = popOrThrow()
                        curr.stack += Arrmake(length.asIntT(), initializer.asBoxedArrayT())
                    }

                    else -> {
                        curr.stack += IntT(ip) // treat instruction pointer as an integer
                        next = labelIndex(op.name)
                    }
                }
            }

            is Enter -> {
                val returnAddress = popOrThrow()
                curr.enter()
                for (param in op.params) {
                    curr.stackEnvironment += (param to popOrThrow())
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