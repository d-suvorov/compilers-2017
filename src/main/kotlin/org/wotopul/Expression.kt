package org.wotopul

import org.wotopul.Expr.*
import org.wotopul.Primitive.*

fun eval(expr: Expr, conf: Configuration): Pair<Configuration, Primitive> = when (expr) {
    is Const -> Pair(conf, IntT(expr.value))

    is Variable -> {
        val value = (conf.environment[expr.name]
            ?: throw ExecutionException("undefined variable: ${expr.name}"))
        Pair(conf, value)
    }

    is Binop -> {
        val (afterLeft, left) = eval(expr.lhs, conf)
        val (afterRight, right) = eval(expr.rhs, afterLeft)
        Pair(afterRight, evalBinary(expr.op, left, right))
    }

    is FunctionExpr -> evalFunction(expr.function, conf)

    is CharLiteral -> Pair(conf, CharT(expr.value))
    is StringLiteral -> Pair(conf, StringT(expr.value))
}

fun evalBinary(op: String, left: Primitive, right: Primitive): Primitive {
    if (left is IntT && right is IntT) {
        return IntT(intBinopByString(op) (left.value, right.value))
    }
    if (left is CharT && right is CharT) {
        return IntT(charBinopByString(op) (left.value, right.value))
    }
    throw ExecutionException(
        "binary operations on ${left.type()} and ${right.type()} are not allowed")
}

fun intBinopByString(op: String): (Int, Int) -> Int = when (op) {
    "*" -> { x, y -> x * y }
    "/" -> { x, y -> x / y }
    "%" -> { x, y -> x % y }

    "+" -> { x, y -> x + y }
    "-" -> { x, y -> x - y }

    "&&" -> { x, y -> (x.toBoolean() && y.toBoolean()).toInt() }
    "||" -> { x, y -> (x.toBoolean() || y.toBoolean()).toInt() }

    else -> cmpBinopByString(op)
        ?: throw AssertionError("unknown character binary operation")
}

fun charBinopByString(op: String): (Char, Char) -> Int =
    cmpBinopByString(op) ?: throw AssertionError("unknown character binary operation")

fun <T : Comparable<T>> cmpBinopByString(op: String): ((T, T) -> Int)? =
    when(op) {
        "<" -> { x, y -> (x < y).toInt() }
        "<=" -> { x, y -> (x <= y).toInt() }
        ">" -> { x, y -> (x > y).toInt() }
        ">=" -> { x, y -> (x >= y).toInt() }

        "==" -> { x, y -> (x == y).toInt() }
        "!=" -> { x, y -> (x != y).toInt() }

        else -> null
    }

fun Boolean.toInt(): Int = if (this) 1 else 0
fun Int.toBoolean(): Boolean = this != 0