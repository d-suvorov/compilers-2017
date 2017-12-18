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

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other?.javaClass != javaClass) return false

                other as Register
                return idx == other.idx
            }

            override fun hashCode(): Int {
                return idx
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

class X86FunctionContext(val name: String, val params: List<String>, val locals: Set<String>) {
    private val localsSize = locals.size
    var tempSize = 0

    val frameSize get() = localsSize + tempSize
    val isStackFrameOpen get() = frameSize != 0

    private val symbolStack: MutableList<Operand> = mutableListOf()

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

        // Assign slots to locals
        var offset = 1
        for (local in locals) {
            localsSlots[local] = Operand.Stack(offset++)
        }

        // Assign slots to params
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
        return if (idx < eaxIdx) {
            Register(idx)
        } else {
            Operand.Stack(localsSize - eaxIdx + idx + 1)
        }
    }

    fun push(): Operand {
        fun next(size: Int): Operand =
            when (size) {
                // TODO can the cut'paste be eliminated (#get)
                in 0 until eaxIdx -> Register(size)
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

fun compile(program: List<StackOp>, ast: Program, mtrace: Boolean = false): String {
    fun emitFunctionCall(out: MutableList<X86Instr>, name: String, vararg args: Operand) {
        // TODO save registers only when it is necessary and save only necessary amount of registers
        // Save registers
        for (i in 0 until eaxIdx)
            out += Push(Register(i))
        // Push arguments
        for (arg in args.reversed())
            out += Push(arg)
        // Call function
        out += Call(name)
        // Pop arguments
        for (i in args.indices)
            // Actual operand doesn't matter because the value is not used
            out += Pop(edx)
        // Restore registers
        for (i in eaxIdx - 1 downTo 0)
            out += Pop(Register(i))
    }

    fun modifyCounter(out: MutableList<X86Instr>, objPointer: Operand, increase: Boolean) {
        val intrinsic = if (increase) "_increase_count" else "_decrease_count"
        emitFunctionCall(out, intrinsic, objPointer)
    }

    // TODO find an appropriate place for it
    val mainLocals = mutableSetOf<String>()
    val mainIndex = program.indexOfFirst { it is StackOp.Label && it.name == mainLabel }
    for (i in mainIndex .. program.lastIndex) {
        val op = program[i]
        // TODO do we need also `Load`
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
                // Does not hold because of how assignment/reads to arrays are compiled
                // TODO review
                // assert(top == Register(0))
                out += listOf(
                    Call("read"),
                    Move(eax, top)
                )
            }

            is StackOp.Write -> {
                val top = conf.pop()
                assert(top == Register(0))
                out += listOf(
                    Push(top),
                    Call("write"),
                    Pop(top) // TODO does opnd matter here?
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
                    emitFunctionCall(out, "_strdup_raw", opnd)
                    // Put return value on a symbol stack
                    out += Move(eax, top)
                } else {
                    val opnd = Operand.Literal(op.value.toInt())
                    out += Move(opnd, top)
                }
            }

            is StackOp.Pop -> {
                val top = conf.pop()
                modifyCounter(out, top, increase = false)
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
                modifyCounter(out, top, increase = true)
            }

            is StackOp.Store -> {
                val top = conf.pop()
                val slot = conf.variableSlot(op.name)
                assert(top == Register(0))

                modifyCounter(out, slot, increase = false)
                out += Move(top, slot)
                // The reference counter for stored object does not change
            }

            is StackOp.LoadArr -> {
                emitFunctionCall(out, "_arrget_wrapper", conf.get(0), conf.get(1))

                // Pop array and index from stack
                conf.pop()
                conf.pop()

                val top = conf.push()
                out += Move(eax, top)
                modifyCounter(out, top, increase = true)
            }

            is StackOp.StoreArr -> {
                emitFunctionCall(out, "_arrset_wrapper",
                    conf.get(0), conf.get(1), conf.get(2))
                // Pop index and value from stack
                // Leave array on stack
                conf.pop()
                conf.pop()
                // Ignore return value
            }

            is StackOp.MakeUnboxedArray, StackOp.MakeBoxedArray -> {
                emitFunctionCall(out, "_arrmake_impl_wrapper", conf.top())
                // Pop length from symbol stack
                conf.pop()
                // Put return value on a symbol stack
                out += Move(eax, conf.push())
            }

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
                        // Balance left shift added by multiplication
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
                            // Balance right shift added by division
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

                val name = when {
                    op.name in stringIntrinsics() -> stringIntrinsicWrapperName(op.name)
                    op.name in arrayIntrinsics() -> arrayIntrinsicWrapperName(op.name)
                    else -> op.name
                }
                // TODO throw an exception if function is undefined
                val nArgs = when {
                    op.name in stringIntrinsics() -> stringIntrinsicNArgs(op.name)
                    op.name in arrayIntrinsics() -> arrayIntrinsicNArgs(op.name)
                    else -> ast.functionDefinitionByName(op.name)!!.params.size
                }
                val args = (0 until nArgs).map { conf.get(it) }
                emitFunctionCall(out, name, *args.toTypedArray())
                // Pop args from symbol stack
                for (i in 0 until nArgs) {
                    conf.pop()
                }
                // Put return value on a symbol stack
                val top = conf.push()
                out += Move(eax, top)
            }

            is StackOp.Enter -> { /* do nothing */ }

            is StackOp.Return -> {
                // For `top` goes out as a return value
                // Before releasing locals to not free return value
                modifyCounter(out, conf.top(), increase = true)

                // Release locals
                for (local in conf.locals + conf.params) {
                    val slot = conf.variableSlot(local)
                    modifyCounter(out, slot, increase = false)
                }

                // Move result to %eax
                assert(conf.top() == Register(0))
                out += Move(conf.pop(), eax)

                // Close stack frame
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

    fun mtrace() {
        val tmp = mutableListOf<X86Instr>()
        emitFunctionCall(tmp, "mtrace")
        tmp.forEach { result.append(it) }
    }

    fun initializeLocals(c: X86FunctionContext) {
        for (local in c.locals - c.params) {
            val slot = c.variableSlot(local)
            result.append(Move(Operand.Literal(0), slot))
        }
    }

    fun acquireParams(c: X86FunctionContext) {
        val tmp = mutableListOf<X86Instr>()
        for (local in c.params) {
            val slot = c.variableSlot(local)
            modifyCounter(tmp, slot, increase = true)
        }
        tmp.forEach { result.append(it) }
    }

    fun closeStackFrame() {
        result.append(Leave)
    }

    fun compileCurrentFunctionBody() {
        openStackFrame(conf!!)
        if (mtrace && conf!!.name == mainLabel) {
            mtrace()
        }
        initializeLocals(conf!!)
        // Code that releases params and locals is emitted before function return
        acquireParams(conf!!)
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
    compileCurrentFunctionBody() // Last function
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