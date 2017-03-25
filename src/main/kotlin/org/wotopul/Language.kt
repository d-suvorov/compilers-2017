package org.wotopul

sealed class AbstractNode {
    sealed class Program : AbstractNode() {
        object Skip : Program()
        class Sequence(val first: Program, val rest: Program) : Program()

        class Assignment(val variable: String, val value: Expr) : Program()
        class Read(val variable: String) : Program()
        class Write(val value: Expr) : Program()

        class If(val condition: Expr, val thenClause: Program, val elseClause: Program): Program()
        class While(val condition: Expr, val body: Program): Program()
    }

    sealed class Expr : AbstractNode() {
        class Const(val value: Int) : Expr()
        class Variable(val name: String) : Expr()
        class Binop(val op: String, val lhs: Expr, val rhs: Expr) : Expr()
    }
}