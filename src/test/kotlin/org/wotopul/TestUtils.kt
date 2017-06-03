package org.wotopul

fun programOf(statement: Statement) = Program(emptyList(), statement)

fun sequence(vararg statements: Statement): Statement {
    fun sequence(statements: List<Statement>): Statement =
        when (statements.size) {
            0 -> Statement.Skip
            else -> Statement.Sequence(
                statements.first(),
                sequence(statements.subList(1, statements.size))
            )
        }

    return sequence(statements.toList())
}

operator fun Expr.plus(rhs: Expr) = Expr.Binop("+", this, rhs)
operator fun Expr.minus(rhs: Expr) = Expr.Binop("-", this, rhs)
operator fun Expr.times(rhs: Expr) = Expr.Binop("*", this, rhs)
operator fun Expr.div(rhs: Expr) = Expr.Binop("/", this, rhs)

fun read(name: String) = Statement.Read(Expr.Variable(name))
fun assignment(name: String, expr: Expr) = Statement.Assignment(Expr.Variable(name), expr)