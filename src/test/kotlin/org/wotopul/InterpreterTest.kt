package org.wotopul

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.wotopul.LogicalExpr.ArithExpr.Const
import org.wotopul.LogicalExpr.ArithExpr.Variable
import org.wotopul.Program.*
import kotlin.test.assertEquals

@RunWith(Parameterized::class)
class HelloTest(val program : Program, val input: List<Int>, val output: List<Int>) {
    companion object {
        @JvmStatic
        val SEQUENCE_OF_WRITES = sequence(
            Write(Const(41)),
            Write(Const(42)),
            Write(Const(43))
        )

        @JvmStatic
        val SIMPLE_ARITHMETIC = Write(
            (Const(5) / Const(2) + Const(2)) * Const(42)
        )

        @JvmStatic
        val WRITES_AND_READS = sequence(
            Assignment("x", Const(1)),
            Write(Variable("x")),
            Assignment("x", Const(2)),
            Assignment("y", Const(3)),
            Write(Variable("y")),
            Write(Variable("x")),
            Read("y"),
            Read("y"),
            Write(Variable("x")),
            Write(Variable("y")),
            Read("y"),
            Write(Variable("y"))
        )

        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> = listOf(
            arrayOf(Program.Skip, listOf(1, 2, 3), emptyList<Int>()),
            arrayOf(SEQUENCE_OF_WRITES, emptyList<Int>(), listOf(41, 42, 43)),
            arrayOf(SIMPLE_ARITHMETIC, emptyList<Int>(), listOf((5 / 2 + 2) * 42)),
            arrayOf(WRITES_AND_READS, listOf(7, 42, 0), listOf(1, 3, 2, 2, 42, 0))
        )
    }

    @Test
    fun testImpl() {
        assertEquals(output, interpret(program, input))
    }
}
