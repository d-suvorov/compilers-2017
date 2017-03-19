package org.wotopul

import org.wotopul.AbstractNode.Expr
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

operator fun Expr.plus(rhs: Expr) = Expr.Addition(this, rhs)
operator fun Expr.minus(rhs: Expr) = Expr.Subtraction(this, rhs)
operator fun Expr.times(rhs: Expr) = Expr.Multiplication(this, rhs)
operator fun Expr.div(rhs: Expr) = Expr.Division(this, rhs)