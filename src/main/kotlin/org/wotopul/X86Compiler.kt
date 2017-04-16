package org.wotopul

import org.wotopul.StackOp.*
import org.wotopul.X86Instr.Operand

val wordSize = 4

val registers: List<String> = listOf(
    "%ebx", "%ecx", "%esi", "%edi", "%eax", "%edx"
)

val eaxIdx = registers.indexOf("%eax")
val eax = Operand.Register(eaxIdx)

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
            is Stack -> "-${offset * wordSize}(%%ebp)"
        }
    }

    class Binop(val op: String, val opnd1: Operand, val opnd2: Operand) : X86Instr()
    class Move(val src: Operand, val dst: Operand): X86Instr()
    class Call(val name: String): X86Instr()
    class Push(val opnd: Operand): X86Instr()
    class Pop(val opnd: Operand): X86Instr()
    object Ret : X86Instr()

    override fun toString(): String = when (this) {
        is Binop -> when (op) {
            "+" -> "addl\t$opnd1,\t$opnd2"
            "*" -> "imull\t$opnd1,\t$opnd2"
            else -> TODO("unsupported operation: $op")
        }

        is Move -> "movl\t$src,\t$dst"

        is Call -> "call\t$name"

        is Push -> "pushl\t$opnd"

        is Pop -> "popl\t$opnd"

        is Ret -> "ret"
    }
}

class X86Configuration(
    val locals: MutableSet<String> = mutableSetOf(),
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
                else -> Operand.Stack(size - eaxIdx)
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
                        X86Instr.Call("write")
                        // TODO push return value of `write` to a symbol stack?
                    )
                }

                is Push -> {
                    val top = conf.push()
                    result += X86Instr.Move(Operand.Literal(op.value), top)
                }

                is Load -> TODO("unimplemented yet")
                is Store -> TODO("unimplemented yet")

                is Binop -> {
                    val opnd = conf.pop()
                    val dst = conf.top()
                    result += X86Instr.Binop(op.op, opnd, dst)
                }

                is Label -> TODO("unimplemented yet")
                is Jump -> TODO("unimplemented yet")
                is Jnz -> TODO("unimplemented yet")
            }
        }

        program.forEach { compile(it) }
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

    return "${header()}${body()}${footer()}"
}