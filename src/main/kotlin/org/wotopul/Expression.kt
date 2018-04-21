package org.wotopul

import org.wotopul.Expr.*
import org.wotopul.VarValue.*

fun evalArray(conf: Configuration, variable: Variable, depth: Int): Pair<Configuration, VarValue> {
    val array: VarValue = conf.environment[variable.name]
        ?: throw ExecutionException("undefined name: ${variable.name}")

    if (depth == 0) {
        return Pair(conf, array)
    } else {
        val (conf1, value) = evalArray(conf, variable, depth - 1)
        val (conf2, index) = eval(variable.indices[depth - 1], conf1)
        return when (value) {
            is UnboxedArrayT -> Pair(conf2, IntT(value.get(index.toInt())))
            is BoxedArrayT -> Pair(conf2, value.get(index.toInt()) as VarValue)
            else -> throw ExecutionException("cannot index primitive value")
        }
    }
}

fun eval(expr: Expr, conf: Configuration): Pair<Configuration, VarValue> = when (expr) {
    is Const -> Pair(conf, IntT(expr.value))

    is Variable -> {
        val regularVariable = conf.environment.containsKey(expr.name)
        if (regularVariable) {
            evalArray(conf, expr, expr.indices.size)
        } else {
            val tableIndex = conf.functionTable.getIndex(expr.name)
                ?: throw ExecutionException("invalid function pointer value: $expr.name")
            Pair(conf, FunctionPointerT(tableIndex))
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

    is BoxedArrayInitializer -> {
        val arr = Array<ReferenceT?>(expr.exprList.size, { null })
        var curr = conf
        for ((i, e) in expr.exprList.withIndex()) {
            val (next, item) = eval(e, curr)
            if (item !is ReferenceT)
                throw ExecutionException("primitive values cannot be stored in a boxed array")
            arr[i] = item
            curr = next
        }
        Pair(curr, BoxedArrayT(arr))
    }
}

fun evalBinary(op: String, left: VarValue, right: VarValue): VarValue {
    /*if (left is IntT && right is IntT) {
        return IntT(intBinopByString(op) (left.value, right.value))
    }*/
    if (left is CharT && right is CharT) {
        return IntT(charBinopByString(op) (left.value, right.value))
    }
    return IntT(intBinopByString(op) (left.toInt(), right.toInt()))
    /*throw ExecutionException(
        "binary operations on ${left.type()} and ${right.type()} are not allowed")*/
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