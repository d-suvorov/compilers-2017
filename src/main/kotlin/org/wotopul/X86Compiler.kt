package org.wotopul

import org.wotopul.X86Instr.*
import org.wotopul.X86Instr.Operand.Register

val mainLabel = "main"

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
        class Literal(val value: Int, val marked: Boolean = true) : Operand()
        class StringLiteral(val label: String) : Operand()
        class Variable(val name: String) : Operand()
        class Stack(val offset: Int) : Operand()

        override fun toString(): String = when (this) {
            is Literal -> {
                // "signed" left shift sets LSB to 0 marking value as primitive
                "\$${if (marked) value * 2 else value}"
            }
            is Register -> registers[idx]
            is StringLiteral -> "\$$label"
            is Variable -> name
            is Stack -> "${-offset * wordSize}(%ebp)"
        }
    }

    class Binop(val op: String, val opnd1: Operand, val opnd2: Operand) : X86Instr()
    class Binop16(val op: String, val opnd1: String, val opnd2: String) : X86Instr()
    class Div(val opnd: Operand) : X86Instr()

    class Move(val src: Operand, val dst: Operand) : X86Instr()

    class Push(val opnd: Operand) : X86Instr()
    class Pop(val opnd: Operand) : X86Instr()
    object Pusha : X86Instr()
    object Popa : X86Instr()

    class Call(val name: String) : X86Instr()
    object Ret : X86Instr()
    object Leave : X86Instr()

    class Label(val name: String) : X86Instr()
    class Jmp(val label: String) : X86Instr()
    class Jnz(val label: String) : X86Instr()

    class SetCC(val op: String, val dst: String) : X86Instr()

    object Cltd : X86Instr()

    class Sar(val opnd: Operand, val count: Operand) : X86Instr()

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
            is Pusha -> "pushal"
            is Popa -> "popal"

            is Call -> "call\t$name"
            is Ret -> "ret"
            is Leave -> "leave"

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

            is Sar -> "sarl\t$count,\t$opnd"
        }
        val indent = if (this is Label) "" else "\t"
        return "$indent$str\n"
    }
}

fun X86FunctionContext(function: FunctionDefinition) =
    X86FunctionContext(function.name, function.params, function.locals)

class X86FunctionContext(val name: String, params: List<String>, val locals: Set<String>) {
    private val localsSize = locals.size
    var tempSize = 0

    val frameSize get() = localsSize + tempSize
    val isStackFrameOpen get() = frameSize != 0

    val symbolStack: MutableList<Operand> = mutableListOf()

    private val localsSlots: MutableMap<String, Operand> = mutableMapOf()

    init {
        /*
                   | args
                   | return address
            ebp -> | saved %ebp
                   | [local-0]       -- if stack frame is open
                   | ...
            esp -> | [local-n]
                   | symbol stack
                   v
         */

        // assign slots to locals
        var offset = 1
        for (local in locals) {
            localsSlots[local] = Operand.Stack(offset++)
        }

        // assign slots to params
        offset = -2
        for (param in params) {
            localsSlots[param] = Operand.Stack(offset--)
        }
    }

    fun variableSlot(name: String) = localsSlots[name]!!

    fun top(): Operand = symbolStack.last()

    // TODO document
    fun get(offset: Int): Operand {
        val idx = symbolStack.size - offset - 1
        if (idx < 0)
            throw AssertionError("cannot find argument on stack")
        if (idx < eaxIdx) {
            return Register(idx)
        } else {
            return Operand.Stack(localsSize - eaxIdx + idx + 1)
        }
    }

    fun push(): Operand {
        fun next(size: Int): Operand =
            when (size) {
                // TODO can the cut'paste be eliminated (#get)
                in 0 .. eaxIdx - 1 -> Register(size)
                else -> {
                    val stackOffset = size - eaxIdx
                    tempSize = maxOf(tempSize, stackOffset + 1)
                    Operand.Stack(localsSize + stackOffset + 1)
                }
            }

        val top = next(symbolStack.size)
        symbolStack += top
        return top
    }

    fun pop() = symbolStack.removeAt(symbolStack.lastIndex)
}

fun compile(program: List<StackOp>, ast: Program): String {
    // TODO find an appropriate place for it
    val mainLocals = mutableSetOf<String>()
    val mainIndex = program.indexOfFirst { it is StackOp.Label && it.name == mainLabel }
    for (i in mainIndex .. program.lastIndex) {
        val op = program[i]
        if (op is StackOp.Load)
            mainLocals += op.name
        if (op is StackOp.Store)
            mainLocals += op.name
    }

    val stringLiteralsByLabel = mutableListOf<Pair<String, String>>()

    fun compile(op: StackOp, conf: X86FunctionContext, out: MutableList<X86Instr>) {
        when (op) {
            is StackOp.Nop -> { /* very optimizing compiler */ }

            is StackOp.Read -> {
                val top = conf.push()
                assert(top == Register(0))
                out += listOf(
                    Call("read"),
                    // "signed" left shift sets LSB to 0 marking value as primitive
                    Binop("*", Operand.Literal(2, marked = false), eax),
                    Move(eax, top)
                )
            }

            is StackOp.Write -> {
                val top = conf.pop()
                assert(top == Register(0))
                out += listOf(
                    // convert from special representation with marked LSB
                    Sar(top, Operand.Literal(1, marked = false)),
                    Push(top),
                    Call("write"),
                    Pop(top)
                )
            }

            is StackOp.Push -> {
                val top = conf.push()
                if (op.value is VarValue.StringT) {
                    val label = "_internal_string_${stringLiteralsByLabel.size}"
                    stringLiteralsByLabel += label to String(op.value.value)
                    val opnd = Operand.StringLiteral(label)
                    // Call `strdup` to extract mutable string
                    // This hack will hopefully go away with GC introduction
                    // TODO save registers only when it is necessary and save only necessary amount of registers
                    // Save registers
                    for (i in 0 .. eaxIdx - 1)
                        out += Push(Register(i))
                    // Push arguments
                    out += Push(opnd)
                    // Call function
                    out += Call("_strdup_raw")
                    // Pop arguments
                    out += Pop(edx) // actual operand doesn't matter because the value is not used
                    // Restore registers
                    for (i in eaxIdx - 1 downTo 0)
                        out += Pop(Register(i))
                    // Put return value on a symbol stack
                    out += Move(eax, top)
                } else {
                    val opnd = Operand.Literal(op.value.toInt())
                    out += Move(opnd, top)
                }
            }

            is StackOp.Pop -> {
                val top = conf.pop()

                /*// Call `_decrease_count`
                // TODO save registers only when it is necessary and save only necessary amount of registers
                // Save registers
                for (i in 0 .. eaxIdx - 1)
                    out += Push(Register(i))
                // Push arguments
                out += Push(top)
                // Call function
                out += Call("_decrease_count")
                // Pop arguments
                out += Pop(edx) // actual operand doesn't matter because the value is not used
                // Restore registers
                for (i in eaxIdx - 1 downTo 0)
                    out += Pop(Register(i))*/
            }

            is StackOp.Load -> {
                val top = conf.push()
                if (top is Register) {
                    out += Move(conf.variableSlot(op.name), top)
                } else {
                    out += listOf(
                        Move(conf.variableSlot(op.name), edx),
                        Move(edx, top)
                    )
                }
            }

            is StackOp.Store -> {
                val top = conf.pop()
                val slot = conf.variableSlot(op.name)
                assert(top == Register(0))

                /*if (op.name[0].isUpperCase()) {
                    // Call `_decrease_count`
                    // TODO save registers only when it is necessary and save only necessary amount of registers
                    // Save registers
                    for (i in 0..eaxIdx - 1)
                        out += Push(Register(i))
                    // Push arguments
                    out += Push(slot)
                    // Call function
                    out += Call("_decrease_count")
                    // Pop arguments
                    out += Pop(edx) // actual operand doesn't matter because the value is not used
                    // Restore registers
                    for (i in eaxIdx - 1 downTo 0)
                        out += Pop(Register(i))
                }*/

                out += Move(top, slot)

                /*if (op.name[0].isUpperCase()) {
                    // Call `_increase_count`
                    // TODO save registers only when it is necessary and save only necessary amount of registers
                    // Save registers
                    for (i in 0..eaxIdx - 1)
                        out += Push(Register(i))
                    // Push arguments
                    out += Push(top)
                    // Call function
                    out += Call("_increase_count")
                    // Pop arguments
                    out += Pop(edx) // actual operand doesn't matter because the value is not used
                    // Restore registers
                    for (i in eaxIdx - 1 downTo 0)
                        out += Pop(Register(i))
                }*/
            }

            is StackOp.LoadArr -> {
                for (i in 0..eaxIdx - 1)
                    out += (Push(Register(i)))
                // Push arguments
                out += (Push(conf.get(1)))
                out += (Push(conf.get(0)))
                // Call function
                out += (Call("_arrget"))
                // Pop arguments
                out += (Pop(edx)) // actual operand doesn't matter because the value is not used
                out += (Pop(edx)) // actual operand doesn't matter because the value is not used
                // Restore registers
                for (i in eaxIdx - 1 downTo 0)
                    out += (Pop(Register(i)))
                conf.pop()
                conf.pop()
                val top = conf.push()
                out += Move(eax, top)
            }

            is StackOp.StoreArr -> {
                for (i in 0..eaxIdx - 1)
                    out += (Push(Register(i)))
                // Push arguments
                out += (Push(conf.get(2)))
                out += (Push(conf.get(1)))
                out += (Push(conf.get(0)))
                // Call function
                out += (Call("_arrset"))
                // Pop arguments
                out += (Pop(edx)) // actual operand doesn't matter because the value is not used
                out += (Pop(edx)) // actual operand doesn't matter because the value is not used
                out += (Pop(edx)) // actual operand doesn't matter because the value is not used
                // Restore registers
                for (i in eaxIdx - 1 downTo 0)
                    out += (Pop(Register(i)))
                conf.pop()
                conf.pop()
                // leave array on stack
                // ignore return value
            }

            is StackOp.MakeUnboxedArray, StackOp.MakeBoxedArray -> {
                for (i in 0..eaxIdx - 1)
                    out += (Push(Register(i)))
                // Push arguments
                out += (Push(conf.top()))
                // Call function
                out += (Call("_arrmake_impl"))
                // Pop arguments
                out += (Pop(edx)) // actual operand doesn't matter because the value is not used
                // Restore registers
                for (i in eaxIdx - 1 downTo 0)
                    out += (Pop(Register(i)))
                // Pop length from symbol stack
                conf.pop()
                // Put return value on a symbol stack
                val top = conf.push()
                out += Move(eax, top)
            }

            /*is StackOp.MakeBoxedArray -> {
                for (i in 0..eaxIdx - 1)
                    out += (Push(Register(i)))
                // Push arguments
                out += (Push(conf.top()))
                // Call function
                out += (Call("_arrmake_impl"))
                // Pop arguments
                out += (Pop(edx)) // actual operand doesn't matter because the value is not used
                // Restore registers
                for (i in eaxIdx - 1 downTo 0)
                    out += (Pop(Register(i)))
                // Pop length from symbol stack
                conf.pop()
                // Put return value on a symbol stack
                val top = conf.push()
                out += Move(eax, top)
            }*/

            is StackOp.Binop -> {
                val src = conf.pop()
                val dst = conf.top()

                fun compileBinaryImpl(op: String, opnd1: Operand) {
                    if (dst is Register) {
                        out += Binop(op, opnd1, dst)
                    } else {
                        out += listOf(
                            Move(dst, edx),
                            Binop(op, opnd1, edx),
                            Move(edx, dst)
                        )
                    }
                }

                fun compileBinary(op: String) {
                    compileBinaryImpl(op, src)
                }

                fun convertDstToMarkedPrimitive() {
                    compileBinaryImpl("*", Operand.Literal(2, marked = false))
                }

                when (op.op) {
                    "+", "-" -> compileBinary(op.op)

                    "*" -> {
                        compileBinary(op.op)
                        // balance left shift added by multiplication
                        out += Sar(dst, Operand.Literal(1, marked = false))
                    }

                    "/", "%" -> {
                        out += listOf(
                            Move(dst, eax),
                            Cltd,
                            Div(src),
                            Move(if (op.op == "/") eax else edx, dst)
                        )
                        if (op.op == "/") {
                            // balance right shift added by division
                            convertDstToMarkedPrimitive()
                        }
                    }

                    "&&" -> {
                        out += Binop("xor", eax, eax)

                        fun checkZero(opnd: Operand, res: String) {
                            val opnd1: Register = if (opnd !is Register) {
                                out += Move(opnd, edx)
                                edx
                            } else {
                                opnd
                            }
                            out += listOf(
                                Binop("and", opnd1, opnd1),
                                SetCC("!=", res)
                            )
                        }

                        checkZero(dst, "%al")
                        checkZero(src, "%ah")

                        out += listOf(
                            Binop16("and", "%ah", "%al"),
                            Binop16("xor", "%ah", "%ah"),
                            Move(eax, dst)
                        )
                        convertDstToMarkedPrimitive()
                    }

                    "||" -> {
                        out += Binop("xor", eax, eax)
                        compileBinary("or")
                        out += listOf(
                            SetCC("!=", "%al"),
                            Move(eax, dst)
                        )
                        convertDstToMarkedPrimitive()
                    }

                    "<", "<=", ">", ">=", "==", "!=" -> {
                        out += Binop("xor", eax, eax)
                        compileBinary("cmp")
                        out += listOf(
                            SetCC(op.op, "%al"),
                            Move(eax, dst)
                        )
                        convertDstToMarkedPrimitive()
                    }
                }
            }

            is StackOp.Label -> out += Label(op.name)

            is StackOp.Jump -> out += Jmp(op.label)

            is StackOp.Jnz -> {
                val top = conf.pop()
                assert(top == Register(0))
                out += listOf(
                    Binop("test", top, top),
                    Jnz(op.label)
                )
            }

            is StackOp.Call -> {
                /*var stackOffset = conf.symbolStack.size
                conf.tempSize = maxOf(conf.tempSize, stackOffset)
                // out += Binop("-", esp, Operand.Literal(stackOffset))
                while (!conf.symbolStack.isEmpty()) {
                    out += Move(conf.pop(), Operand.Stack(--stackOffset))
                }
                assert(stackOffset == 0)
                out += Call(op.name)

                // restore stack layout
                // restore registers
                val nArgs = ast.functionDefinitionByName(op.name)!!.params.size
                val nShiftedSlots = stackOffset - nArgs
                for (i in 0 .. nShiftedSlots) {
                    val top = conf.push()
                    out += Move(Operand.Stack(i), top)
                }

                val top = conf.push()
                out += Move(eax, top)*/

                // TODO save registers only when it is necessary and save only necessary amount of registers
                // Save registers
                for (i in 0 .. eaxIdx - 1)
                    out += Push(Register(i))

                // Push arguments
                // TODO throw an exception if function is undefined
                val nArgs = if (op.name in stringIntrinsics())
                    stringIntrinsicNArgs(op.name)
                else if (op.name in arrayIntrinsics())
                    arrayIntrinsicNArgs(op.name)
                else
                    ast.functionDefinitionByName(op.name)!!.params.size
                for (offset in nArgs - 1 downTo 0)
                    out += Push(conf.get(offset))

                // Call function
                val name = if (op.name in stringIntrinsics())
                    stringIntrinsicWrapperName(op.name)
                else if (op.name in arrayIntrinsics())
                    arrayIntrinsicWrapperName(op.name)
                else op.name
                out += Call(name)

                // Pop arguments
                for (i in 0 .. nArgs - 1) {
                    out += Pop(edx) // actual operand doesn't matter because the value is not used
                    conf.pop()
                }

                // Restore registers
                for (i in eaxIdx - 1 downTo 0)
                    out += Pop(Register(i))

                // Put return value on a symbol stack
                val top = conf.push()
                out += Move(eax, top)
            }

            is StackOp.Enter -> { /* do nothing */ }

            is StackOp.Return -> {
                // move result to %eax
                assert(conf.top() == Register(0))
                out += Move(conf.pop(), eax)

                // close stack frame
                out += Leave

                out += Ret
            }
        }
    }

    var conf: X86FunctionContext? = null
    var nextCtx: X86FunctionContext? = null

    /**
     * TODO
     */
    fun functionStart(op: StackOp): Boolean {
        if (op !is StackOp.Label)
            return false
        val function = ast.functionDefinitionByName(op.name)
        if (function != null) {
            nextCtx = X86FunctionContext(function)
            return true
        }
        if (op.name == mainLabel) {
            nextCtx = X86FunctionContext(mainLabel, emptyList(), mainLocals)
            return true
        }
        return false
    }

    val body = mutableListOf<X86Instr>()
    val result = StringBuilder()

    fun openStackFrame(c: X86FunctionContext) {
        with(result) {
            append(Push(ebp))
            append(Move(esp, ebp))
            append(Binop("-", Operand.Literal(wordSize * c.frameSize), esp))
        }
    }

    fun initializeLocals(c: X86FunctionContext) {
        c.locals.map { c.variableSlot(it) }
            .forEach { result.append(Move(Operand.Literal(0), it)) }
    }

    fun freeLocals(c: X86FunctionContext) {
        with (result) {
            for (local in c.locals) {
                val slot = c.variableSlot(local)
                // Call `_decrease_count`
                // TODO save registers only when it is necessary and save only necessary amount of registers
                // Save registers
                for (i in 0..eaxIdx - 1)
                    append(Push(Register(i)))
                // Push arguments
                append(Push(slot))
                // Call function
                append(Call("_decrease_count"))
                // Pop arguments
                append(Pop(edx)) // actual operand doesn't matter because the value is not used
                // Restore registers
                for (i in eaxIdx - 1 downTo 0)
                    append(Pop(Register(i)))
            }
        }
    }

    fun closeStackFrame() {
        result.append(Leave)
    }

    fun compileCurrentFunctionBody() {
        openStackFrame(conf!!)
        // initializeLocals(conf!!) -- overwrites params
        body.forEach { result.append(it) }
        body.clear()
    }

    for (op in program) {
        if (functionStart(op)) {
            if (conf != null) {
                compileCurrentFunctionBody()
            }
            conf = nextCtx
            result.append(Label(conf!!.name))
        } else {
            compile(op, conf!!, body)
        }
    }
    compileCurrentFunctionBody() // last function
    closeStackFrame()

    fun header(): String {
        val sb = StringBuilder()
        with (sb) {
            append("\t.text\n")
            append("\t.globl\tmain\n")
            for (sl in stringLiteralsByLabel) {
                append(Label(sl.first))
                append("\t.ascii \"${sl.second}\\0\"\n")
            }
        }
        return sb.toString()
    }

    fun footer(): String =
        "${Binop("xor", eax, eax)}" + "$Ret"

    return "${header()}$result${footer()}"
}