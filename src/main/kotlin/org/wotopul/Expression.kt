package org.wotopul

import org.wotopul.AbstractNode.LogicalExpr.ArithExpr

fun eval(expr: ArithExpr, env: Map<String, Int>): Int? = when (expr) {
    is ArithExpr.Const -> expr.value
    is ArithExpr.Variable -> env[expr.name]

    is ArithExpr.Addition -> evalBinary(expr.lhs, expr.rhs, env, ::eval, { x, y -> x + y })
    is ArithExpr.Subtraction -> evalBinary(expr.lhs, expr.rhs, env, ::eval, { x, y -> x - y })
    is ArithExpr.Multiplication -> evalBinary(expr.lhs, expr.rhs, env, ::eval, { x, y -> x * y })
    is ArithExpr.Division -> evalBinary(expr.lhs, expr.rhs, env, ::eval, { x, y -> x / y })
}

fun <Sub, SubVal, Val> evalBinary(lhs: Sub, rhs: Sub, env: Map<String, Int>,
                                  evalSub: (Sub, Map<String, Int>) -> SubVal?,
                                  binOp: (SubVal, SubVal) -> Val): Val?
{
    val left = evalSub(lhs, env)
    val right = evalSub(rhs, env)
    if (left == null || right == null)
        return null
    return binOp(left, right)
}