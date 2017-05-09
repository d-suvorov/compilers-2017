package org.wotopul

open class AbstractNode

class Program(
    val functions: List<FunctionDefinition>,
    val main: Statement
)
    : AbstractNode()
{
    fun functionDefinitionByName(name: String) =
        functions.find { it.name == name }
}

class FunctionDefinition(
    val name: String,
    val params: List<String>,
    val body: Statement,
    val locals: Set<String>
) : AbstractNode()

class FunctionCall(val name: String, val args: List<Expr>)

sealed class Statement : AbstractNode() {
    object Skip : Statement()
    class Sequence(val first: Statement, val rest: Statement) : Statement()

    class Assignment(val variable: String, val value: Expr) : Statement()
    class Read(val variable: String) : Statement()
    class Write(val value: Expr) : Statement()

    class If(val condition: Expr, val thenClause: Statement, val elseClause: Statement) : Statement()
    class While(val condition: Expr, val body: Statement) : Statement()
    class Repeat(val body: Statement, val condition: Expr) : Statement()

    class Return(val value: Expr) : Statement()

    class FunctionStatement(val function: FunctionCall) : Statement()
}

sealed class Expr : AbstractNode() {
    class Const(val value: Int) : Expr()
    class Variable(val name: String) : Expr()
    class FunctionExpr(val function: FunctionCall) : Expr()
    class Binop(val op: String, val lhs: Expr, val rhs: Expr) : Expr()
}