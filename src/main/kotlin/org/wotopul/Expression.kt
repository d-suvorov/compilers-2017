package org.wotopul

import org.wotopul.Expr.*
import org.wotopul.VarValue.*

fun eval(expr: Expr, conf: Configuration): Pair<Configuration, VarValue> = when (expr) {
    is Const -> Pair(conf, IntT(expr.value))

    is Variable -> {
        val variable: VarValue = conf.environment[expr.name]
            ?: throw ExecutionException("undefined variable: ${expr.name}")
        if (!expr.array) {
            Pair(conf, variable)
        } else {
            // TODO cut'n'paste
            val indices = Array(expr.indices.size, { 0 })
            var curr = conf
            for ((i, e) in expr.indices.withIndex()) {
                val (next, item) = eval(e, curr)
                indices[i] = item.toInt()
                curr = next
            }
            assert(indices.size == 1)
            val array = variable as UnboxedArrayT
            val value = IntT(array.value[indices.first()])
            Pair(curr, value)
        }
    }

    is Binop -> {
        val (afterLeft, left) = eval(expr.lhs, conf)
        val (afterRight, right) = eval(expr.rhs, afterLeft)
        Pair(afterRight, evalBinary(expr.op, left, right))
    }

    is FunctionExpr -> evalFunction(expr.function, conf)

    is CharLiteral -> Pair(conf, CharT(expr.value))
    is StringLiteral -> Pair(conf, StringT(expr.value.toCharArray()))

    is UnboxedArrayInitializer -> {
        val arr = Array(expr.exprList.size, { 0 })
        var curr = conf
        for ((i, e) in expr.exprList.withIndex()) {
            val (next, item) = eval(e, curr)
            arr[i] = item.toInt()
            curr = next
        }
        Pair(curr, UnboxedArrayT(arr))
    }

    is BoxedArrayInitializer -> TODO()
}

fun evalBinary(op: String, left: VarValue, right: VarValue): VarValue {
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