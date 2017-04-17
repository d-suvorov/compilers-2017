package org.wotopul

import org.wotopul.StackOp.*
import org.wotopul.X86Instr.Operand

val wordSize = 4

val registers: List<String> = listOf(
    "%ebx", "%ecx", "%esi", "%edi", "%eax", "%edx"
)

val eaxIdx = registers.indexOf("%eax")
val edxIdx = registers.indexOf("%edx")

val eax = Operand.Register(eaxIdx)
val edx = Operand.Register(edxIdx)

sealed class X86Instr {
    sealed class Operand {
        class Register(val idx: Int) : Operand()
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
    class Div(val opnd: Operand) : X86Instr()
    class Move(val src: Operand, val dst: Operand) : X86Instr()
    class Call(val name: String) : X86Instr()
    class Push(val opnd: Operand) : X86Instr()
    class Pop(val opnd: Operand) : X86Instr()
    class SetCC(val op: String, val dst: String) : X86Instr()
    object Ret : X86Instr()
    object Cltd : X86Instr()
    class Binop16(val op: String, val opnd1: String, val opnd2: String) : X86Instr()
    class Label(val name: String) : X86Instr()
    class Jmp(val label: String) : X86Instr()
    class Jnz(val label: String) : X86Instr()

    override fun toString(): String = when (this) {
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

        is Call -> "call\t$name"
        is Ret -> "ret"

        is Cltd -> "cltd"

        is Label -> "$name:"

        is Jmp -> "jmp\t$label"

        is Jnz -> "jnz\t$label"
    }
}

class X86Configuration(
    val locals: MutableSet<String> = mutableSetOf(),
    var frameSize: Int = 0,
    val symbolStack: MutableList<Operand> = mutableListOf()) /* TODO only registers and stack */
{
    fun addLocal(name: String) {
        locals += name
    }

    fun top(): Operand = symbolStack.last()

    fun push(): Operand {
        fun next(size: Int): Operand =
            when (size) {
                in 0 .. eaxIdx - 1 -> Operand.Register(size)
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
                is Nop -> { /* very optimizing compiler */ }

                is Read -> {
                    val top = conf.push()
                    assert(top == Operand.Register(0))
                    result += listOf(
                        X86Instr.Call("read"),
                        X86Instr.Move(eax, top)
                    )
                }

                is Write -> {
                    val top = conf.pop()
                    assert(top == Operand.Register(0))
                    result += listOf(
                        X86Instr.Push(top),
                        X86Instr.Call("write"),
                        X86Instr.Pop(top)
                        // TODO push return value of `write` to a symbol stack?
                    )
                }

                is Push -> {
                    val top = conf.push()
                    result += X86Instr.Move(Operand.Literal(op.value), top)
                }

                is Load -> {
                    conf.addLocal(op.name)
                    val top = conf.push()
                    if (top is Operand.Register) {
                        result += X86Instr.Move(Operand.Variable(op.name), top)
                    } else {
                        result += listOf(
                            X86Instr.Move(Operand.Variable(op.name), edx),
                            X86Instr.Move(edx, top)
                        )
                    }
                }

                is Store -> {
                    conf.addLocal(op.name)
                    val top = conf.pop()
                    assert(top == Operand.Register(0))
                    result += X86Instr.Move(top, Operand.Variable(op.name))
                }

                is Binop -> {
                    when (op.op) {
                        "+", "-", "*" -> {
                            val src = conf.pop()
                            val dst = conf.top()
                            if (dst is Operand.Register) {
                                result += X86Instr.Binop(op.op, src, dst)
                            } else {
                                result += listOf(
                                    X86Instr.Move(dst, edx),
                                    X86Instr.Binop(op.op, src, edx),
                                    X86Instr.Move(edx, dst)
                                )
                            }
                        }

                        "/", "%" -> {
                            val src = conf.pop()
                            val dst = conf.top()
                            result += listOf(
                                X86Instr.Move(dst, eax),
                                X86Instr.Cltd,
                                X86Instr.Div(src),
                                X86Instr.Move(if (op.op == "/") eax else edx, dst)
                            )
                        }

                        "&&" -> {
                            val src = conf.pop()
                            val dst = conf.top()
                            result += X86Instr.Binop("xor", eax, eax)

                            fun checkZero(opnd: Operand, res: String) {
                                val opnd1: Operand.Register = if (opnd !is Operand.Register) {
                                    result += X86Instr.Move(opnd, edx)
                                    edx
                                } else {
                                    opnd
                                }
                                result += listOf(
                                    X86Instr.Binop("and", opnd1, opnd1),
                                    X86Instr.SetCC("!=", res)
                                )
                            }

                            checkZero(dst, "%al")
                            checkZero(src, "%ah")

                            result += listOf(
                                X86Instr.Binop16("and", "%ah", "%al"),
                                X86Instr.Binop16("xor", "%ah", "%ah"),
                                X86Instr.Move(eax, dst)
                            )
                        }

                        "||" -> {
                            val src = conf.pop()
                            val dst = conf.top()
                            result += X86Instr.Binop("xor", eax, eax)
                            if (dst is Operand.Register) {
                                result += X86Instr.Binop("or", src, dst)
                            } else {
                                result += listOf(
                                    X86Instr.Move(dst, edx),
                                    X86Instr.Binop("or", src, edx),
                                    X86Instr.Move(edx, dst)
                                )
                            }
                            result += listOf(
                                X86Instr.SetCC("!=", "%al"),
                                X86Instr.Move(eax, dst)
                            )
                        }

                        "<", "<=", ">", ">=", "==", "!=" -> {
                            val src = conf.pop()
                            val dst = conf.top()
                            result += X86Instr.Binop("xor", eax, eax)
                            if (dst is Operand.Register) {
                                result += X86Instr.Binop("cmp", src, dst)
                            } else {
                                result += listOf(
                                    X86Instr.Move(dst, edx),
                                    X86Instr.Binop("cmp", src, edx),
                                    X86Instr.Move(edx, dst)
                                )
                            }
                            result += listOf(
                                X86Instr.SetCC(op.op, "%al"),
                                X86Instr.Move(eax, dst)
                            )
                        }
                    }
                }

                is Label -> result += X86Instr.Label(op.name)

                is Jump -> result += X86Instr.Jmp(op.label)

                is Jnz -> {
                    val top = conf.pop()
                    assert(top == Operand.Register(0))
                    result += listOf(
                        X86Instr.Binop("test", top, top),
                        X86Instr.Jnz(op.label)
                    )
                }

                is Call -> TODO("unimplemented yet")
                is Enter -> TODO("unimplemented yet")
                is Return -> TODO("unimplemented yet")
            }
        }

        program.forEach(::compile)
        return Pair(result, conf)
    }

    val (res, conf) = compileImpl(program)

    fun header(): String {
        fun variables(): String =
            conf.locals
                .map { "\t.comm\t$it,\t$wordSize,\t$wordSize\n" }
                .fold("") {acc, def -> "$acc\n$def"}

        return """
            |	.text
            |${variables()}
            |	.globl	main
            |main:
            |
            """.trimMargin()
    }

    fun footer(): String = """
        |	xorl	%eax,	%eax
        |	ret
        """.trimMargin()

    fun body(): String = res
        .map { it.toString() }
        .fold("") {acc, instr -> "$acc\t$instr\n"}

    fun openStackFrame(): String =
        if (conf.frameSize == 0) ""
        else """
            |	pushl	%ebp
            |	movl	%esp,	%ebp
            |	subl	$${wordSize * conf.frameSize},	%esp
            |
            """.trimMargin()

    fun closeStackFrame(): String =
        if (conf.frameSize == 0) ""
        else """
            |	leave
            |
            """.trimMargin()

    return "${header()}${openStackFrame()}${body()}${closeStackFrame()}${footer()}"
}