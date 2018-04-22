package org.wotopul

import org.wotopul.Expr.Variable

open class AbstractNode

class Program(
    val functions: List<FunctionDefinition>,
    val main: Statement
)
    : AbstractNode(), FunctionTable
{
    override fun functionDefinition(name: String) =
        functions.find { it.name == name }

    override fun getName(index: Int) = functions[index].name

    override fun getIndex(name: String): Int? =
        functions.indices.firstOrNull { functions[it].name == name }

    override fun isDeclared(name: String) =
        functionDefinition(name) != null
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

    class Assignment(val variable: Variable, val value: Expr) : Statement()
    class Read(val variable: Variable) : Statement()
    class Write(val value: Expr) : Statement()

    class If(val condition: Expr, val thenClause: Statement, val elseClause: Statement) : Statement()
    class While(val condition: Expr, val body: Statement) : Statement()
    class Repeat(val body: Statement, val condition: Expr) : Statement()

    class Return(val value: Expr) : Statement()

    class FunctionStatement(val function: FunctionCall) : Statement()
}

sealed class Expr : AbstractNode() {
    class Const(val value: Int) : Expr()
    class Variable(val name: String, val indices: Array<Expr> = emptyArray()) : Expr() {
        val array: Boolean = !indices.isEmpty()
    }
    class FunctionPointer(val name: String) : Expr()

    class Binop(val op: String, val lhs: Expr, val rhs: Expr) : Expr()

    class CharLiteral(val value: Char) : Expr()
    class StringLiteral(val value: String) : Expr()

    class UnboxedArrayInitializer(val exprList: Array<Expr>) : Expr()
    class BoxedArrayInitializer(val exprList: Array<Expr>) : Expr()

    class FunctionExpr(val function: FunctionCall) : Expr()
}