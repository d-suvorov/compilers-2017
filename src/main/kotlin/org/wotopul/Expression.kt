package org.wotopul

import org.wotopul.Expr.*
import org.wotopul.Expr.FunctionExpr

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

fun eval(expr: Expr, conf: Configuration): Pair<Configuration, Int> = when (expr) {
    is Const -> Pair(conf, expr.value)

    is Variable -> {
        val value = (conf.environment[expr.name]
            ?: throw ExecutionException("undefined variable: ${expr.name}"))
        Pair(conf, value)
    }

    is Binop -> {
        val (afterLeft, left) = eval(expr.lhs, conf)
        val (afterRight, right) = eval(expr.rhs, afterLeft)
        Pair(afterRight, functionByOperation(expr.op) (left, right))
    }

    is FunctionExpr -> evalFunction(expr.function, conf)
}