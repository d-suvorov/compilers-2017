package org.wotopul

import org.wotopul.Expr.*
import org.wotopul.Expr.Function

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

fun eval(expr: Expr, conf: Configuration): Int = when (expr) {
    is Const -> expr.value

    is Variable -> conf.environment[expr.name]
        ?: throw ExecutionException("undefined variable: ${expr.name}")

    is Binop -> {
        val left = eval(expr.lhs, conf)
        val right = eval(expr.rhs, conf)
        functionByOperation(expr.op) (left, right)
    }

    is Function -> TODO("unimplemented yet")
}