package org.wotopul

import org.wotopul.AbstractNode.LogicalExpr.ArithExpr
import org.wotopul.AbstractNode.Program

fun sequence(vararg statements: Program): Program {
    fun sequence(statements: List<Program>): Program =
        when (statements.size) {
            0 -> Program.Skip
            else -> Program.Sequence(
                statements.first(),
                sequence(statements.subList(1, statements.size))
            )
        }

    return sequence(statements.toList())
}

operator fun ArithExpr.plus(rhs: ArithExpr) = ArithExpr.Addition(this, rhs)
operator fun ArithExpr.minus(rhs: ArithExpr) = ArithExpr.Subtraction(this, rhs)
operator fun ArithExpr.times(rhs: ArithExpr) = ArithExpr.Multiplication(this, rhs)
operator fun ArithExpr.div(rhs: ArithExpr) = ArithExpr.Division(this, rhs)