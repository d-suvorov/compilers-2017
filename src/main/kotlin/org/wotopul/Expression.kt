package org.wotopul

import org.wotopul.AbstractNode.Expr
import org.wotopul.AbstractNode.Expr.*

fun functionByOperation(op: String): (Int, Int) -> Int = when (op) {
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

fun Boolean.toInt(): Int = if (this) 1 else 0
fun Int.toBoolean(): Boolean = this != 0

fun eval(expr: Expr, env: Map<String, Int>): Int = when (expr) {
    is Const -> expr.value

    is Variable -> env[expr.name]
        ?: throw ExecutionException("undefined variable: ${expr.name}")

    is Binop -> {
        val left = eval(expr.lhs, env)
        val right = eval(expr.rhs, env)
        functionByOperation(expr.op) (left, right)
    }
}