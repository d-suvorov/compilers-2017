package org.wotopul

import org.wotopul.AbstractNode.Expr
import org.wotopul.AbstractNode.Expr.*

fun eval(expr: Expr, env: Map<String, Int>): Int? = when (expr) {
    is Const -> expr.value
    is Variable -> env[expr.name]

    is Addition -> evalBinary(expr.lhs, expr.rhs, env, ::eval, { x, y -> x + y })
    is Subtraction -> evalBinary(expr.lhs, expr.rhs, env, ::eval, { x, y -> x - y })
    is Multiplication -> evalBinary(expr.lhs, expr.rhs, env, ::eval, { x, y -> x * y })
    is Division -> evalBinary(expr.lhs, expr.rhs, env, ::eval, { x, y -> x / y })
    is Modulus -> evalBinary(expr.lhs, expr.rhs, env, ::eval, { x, y -> x % y })
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