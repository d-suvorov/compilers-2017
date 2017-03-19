package org.wotopul

import org.wotopul.AbstractNode.Expr
import org.wotopul.AbstractNode.Expr.*

fun eval(expr: Expr, env: Map<String, Int>): Int? = when (expr) {
    is Const -> expr.value
    is Variable -> env[expr.name]

    is Binop -> when (expr.op) {
        "+" -> evalBinary(expr.lhs, expr.rhs, env, ::eval, { x, y -> x + y })
        "-" -> evalBinary(expr.lhs, expr.rhs, env, ::eval, { x, y -> x - y })
        "*" -> evalBinary(expr.lhs, expr.rhs, env, ::eval, { x, y -> x * y })
        "/" -> evalBinary(expr.lhs, expr.rhs, env, ::eval, { x, y -> x / y })
        "%" -> evalBinary(expr.lhs, expr.rhs, env, ::eval, { x, y -> x % y })
        else -> throw AssertionError("unknown binary operation")
    }
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