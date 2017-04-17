package org.wotopul

import org.wotopul.X86Instr.*
import org.wotopul.X86Instr.Operand.Register

val wordSize = 4

val registers: List<String> = listOf(
    "%ebx",
    "%ecx",
    "%esi",
    "%edi",
    "%eax",
    "%edx",
    "%esp",
    "%ebp"
)

val eaxIdx = registers.indexOf("%eax")
val edxIdx = registers.indexOf("%edx")
val espIdx = registers.indexOf("%esp")
val ebpIdx = registers.indexOf("%ebp")

val eax = Register(eaxIdx)
val edx = Register(edxIdx)
val esp = Register(espIdx)
val ebp = Register(ebpIdx)

sealed class X86Instr {
    sealed class Operand {
        class Register(val idx: Int) : Operand() {
            init {
                if (idx !in registers.indices)
                    throw AssertionError("bad register index: $idx")
            }
        }
        class Literal(val value: Int) : Operand()
        class Variable(val name: String) : Operand()
        class Stack(val offset: Int) : Operand()

        override fun toString(): String = when (this) {
            is Register -> registers[idx]
            is Literal -> "\$$value"
            is Variable -> name
            is Stack -> "-${offset * wordSize}(%ebp)"
        }
    }

    class Binop(val op: String, val opnd1: Operand, val opnd2: Operand) : X86Instr()
    class Binop16(val op: String, val opnd1: String, val opnd2: String) : X86Instr()
    class Div(val opnd: Operand) : X86Instr()

    class Move(val src: Operand, val dst: Operand) : X86Instr()

    class Push(val opnd: Operand) : X86Instr()
    class Pop(val opnd: Operand) : X86Instr()

    class Call(val name: String) : X86Instr()
    object Ret : X86Instr()

    class Label(val name: String) : X86Instr()
    class Jmp(val label: String) : X86Instr()
    class Jnz(val label: String) : X86Instr()

    class SetCC(val op: String, val dst: String) : X86Instr()

    object Cltd : X86Instr()

    override fun toString(): String {
        val str = when (this) {
            is Binop -> {
                val instrName = when (op) {
                    "+" -> "addl"
                    "-" -> "subl"
                    "*" -> "imull"

                    "and" -> "andl"
                    "or" -> "orl"
                    "xor" -> "xorl"

                    else -> op
                }
                "$instrName\t$opnd1,\t$opnd2"
            }

            is Binop16 -> {
                val instrName = when (op) {
                    "and" -> "and"
                    "xor" -> "xor"
                    else -> throw AssertionError("unknown 16-bit binop: $op")
                }
                "$instrName\t$opnd1,\t$opnd2"
            }

            is Div -> "idivl\t$opnd"

            is Move -> "movl\t$src,\t$dst"

            is Push -> "pushl\t$opnd"
            is Pop -> "popl\t$opnd"

            is Call -> "call\t$name"
            is Ret -> "ret"

            is Label -> "$name:"
            is Jmp -> "jmp\t$label"
            is Jnz -> "jnz\t$label"

            is SetCC -> {
                val cc = when (op) {
                    "<" -> "l"
                    "<=" -> "le"
                    ">" -> "g"
                    ">=" -> "ge"

                    "==" -> "e"
                    "!=" -> "ne"

                    else -> throw AssertionError("unknown comparison operator: $op")
                }
                "set$cc\t$dst"
            }

            is Cltd -> "cltd"
        }
        val indent = if (this is Label) "" else "\t"
        return "$indent$str\n"
    }
}

class X86Configuration(
    val locals: MutableSet<String> = mutableSetOf(),
    var frameSize: Int = 0,
    val symbolStack: MutableList<Operand> = mutableListOf())
{
    fun addLocal(name: String) {
        locals += name
    }

    fun top(): Operand = symbolStack.last()

    fun push(): Operand {
        fun next(size: Int): Operand =
            when (size) {
                in 0 .. eaxIdx - 1 -> Register(size)
                else -> {
                    val stackOffset = size - eaxIdx
                    frameSize = maxOf(frameSize, stackOffset + 1)
                    Operand.Stack(stackOffset)
                }
            }

        val top = next(symbolStack.size)
        symbolStack += top
        return top
    }

    fun pop() = symbolStack.removeAt(symbolStack.lastIndex)
}

fun compile(program: List<StackOp>): String {
    fun compileImpl(program: List<StackOp>): Pair<List<X86Instr>, X86Configuration> {
        val conf = X86Configuration()
        val result = mutableListOf<X86Instr>()

        fun compile(op: StackOp) {
            when (op) {
                is StackOp.Nop -> { /* very optimizing compiler */ }

                is StackOp.Read -> {
                    val top = conf.push()
                    assert(top == Register(0))
                    result += listOf(
                        Call("read"),
                        Move(eax, top)
                    )
                }

                is StackOp.Write -> {
                    val top = conf.pop()
                    assert(top == Register(0))
                    result += listOf(
                        Push(top),
                        Call("write"),
                        Pop(top)
                        // TODO push return value of `write` to a symbol stack?
                    )
                }

                is StackOp.Push -> {
                    val top = conf.push()
                    result += Move(Operand.Literal(op.value), top)
                }

                is StackOp.Load -> {
                    conf.addLocal(op.name)
                    val top = conf.push()
                    if (top is Register) {
                        result += Move(Operand.Variable(op.name), top)
                    } else {
                        result += listOf(
                            Move(Operand.Variable(op.name), edx),
                            Move(edx, top)
                        )
                    }
                }

                is StackOp.Store -> {
                    conf.addLocal(op.name)
                    val top = conf.pop()
                    assert(top == Register(0))
                    result += Move(top, Operand.Variable(op.name))
                }

                is StackOp.Binop -> {
                    val src = conf.pop()
                    val dst = conf.top()

                    fun compileBinary(op: String) {
                        if (dst is Register) {
                            result += Binop(op, src, dst)
                        } else {
                            result += listOf(
                                Move(dst, edx),
                                Binop(op, src, edx),
                                Move(edx, dst)
                            )
                        }
                    }

                    when (op.op) {
                        "+", "-", "*" -> compileBinary(op.op)

                        "/", "%" -> {
                            result += listOf(
                                Move(dst, eax),
                                Cltd,
                                Div(src),
                                Move(if (op.op == "/") eax else edx, dst)
                            )
                        }

                        "&&" -> {
                            result += Binop("xor", eax, eax)

                            fun checkZero(opnd: Operand, res: String) {
                                val opnd1: Register = if (opnd !is Register) {
                                    result += Move(opnd, edx)
                                    edx
                                } else {
                                    opnd
                                }
                                result += listOf(
                                    Binop("and", opnd1, opnd1),
                                    SetCC("!=", res)
                                )
                            }

                            checkZero(dst, "%al")
                            checkZero(src, "%ah")

                            result += listOf(
                                Binop16("and", "%ah", "%al"),
                                Binop16("xor", "%ah", "%ah"),
                                Move(eax, dst)
                            )
                        }

                        "||" -> {
                            result += Binop("xor", eax, eax)
                            compileBinary("or")
                            result += listOf(
                                SetCC("!=", "%al"),
                                Move(eax, dst)
                            )
                        }

                        "<", "<=", ">", ">=", "==", "!=" -> {
                            result += Binop("xor", eax, eax)
                            compileBinary("cmp")
                            result += listOf(
                                SetCC(op.op, "%al"),
                                Move(eax, dst)
                            )
                        }
                    }
                }

                is StackOp.Label -> result += Label(op.name)

                is StackOp.Jump -> result += Jmp(op.label)

                is StackOp.Jnz -> {
                    val top = conf.pop()
                    assert(top == Register(0))
                    result += listOf(
                        Binop("test", top, top),
                        Jnz(op.label)
                    )
                }

                is StackOp.Call -> TODO("unimplemented yet")
                is StackOp.Enter -> TODO("unimplemented yet")
                is StackOp.Return -> TODO("unimplemented yet")
            }
        }

        program.forEach(::compile)
        return Pair(result, conf)
    }

    val (body, conf) = compileImpl(program)

    fun header(): String {
        val sb = StringBuilder("\t.text\n")
        conf.locals.forEach {
            sb.append("\t.comm\t$it,\t$wordSize,\t$wordSize\n")
        }
        sb.append("\t.globl\tmain\n")
            .append(Label("main"))
        return sb.toString()
    }

    fun footer(): String =
        "${Binop("xor", eax, eax)}" +
        "${Ret}"

    fun body(): String {
        val sb = StringBuilder()
        body.forEach { sb.append(it) }
        return sb.toString()
    }

    fun openStackFrame(): String =
        if (conf.frameSize == 0) ""
        else "${Push(ebp)}" +
            "${Move(esp, ebp)}" +
            "${Binop("sub", Operand.Literal(wordSize * conf.frameSize), esp)}"

    fun closeStackFrame(): String =
        if (conf.frameSize == 0) "" else "\tleave\n"

    return "${header()}${openStackFrame()}${body()}${closeStackFrame()}${footer()}"
}