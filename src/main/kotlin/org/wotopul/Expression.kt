package org.wotopul

import org.wotopul.AbstractNode.Expr
import org.wotopul.AbstractNode.Expr.*

fun eval(expr: Expr, env: Map<String, Int>): Int? = when (expr) {
    is Const -> expr.value
    is Variable -> env[expr.name]

    is Binop -> {
        val f: (Int, Int) -> Int = when (expr.op) {
            "*" -> { x, y -> x * y }
            "/" -> { x, y -> x / y }

            "%" -> { x, y -> x % y }
            "+" -> { x, y -> x + y }
            "-" -> { x, y -> x - y }

            "<" -> { x, y -> (x < y).toInt() }
            "<=" -> { x, y -> (x <= y).toInt() }
            ">" -> { x, y -> (x > y).toInt() }
            ">=" -> { x, y -> (x >= y).toInt() }

            "==" -> { x, y -> (x == y).toInt() }
            "!=" -> { x, y -> (x != y).toInt() }

            "&&" -> { x, y -> (x.toBoolean() && y.toBoolean()).toInt() }
            "||" -> { x, y -> (x.toBoolean() || y.toBoolean()).toInt() }

            else -> throw AssertionError("unknown binary operation")
        }
        evalBinary(expr, env, f)
    }
}

fun Boolean.toInt(): Int = if (this) 1 else 0
fun Int.toBoolean(): Boolean = this != 0

fun evalBinary(binop: Binop, env: Map<String, Int>, binOp: (Int, Int) -> Int): Int? {
    val left = eval(binop.lhs, env)
    val right = eval(binop.rhs, env)
    if (left == null || right == null)
        return null
    return binOp(left, right)
}