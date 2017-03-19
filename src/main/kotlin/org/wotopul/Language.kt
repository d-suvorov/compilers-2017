package org.wotopul

sealed class AbstractNode {
    sealed class Program : AbstractNode() {
        object Skip : Program()
        class Sequence(val first: Program, val rest: Program) : Program()
        class Assignment(val variable: String, val value: Expr) : Program()
        class Read(val variable: String) : Program()
        class Write(val value: Expr) : Program()
    }

    sealed class Expr : AbstractNode() {
        class Const(val value: Int) : Expr()
        class Variable(val name: String) : Expr()

        class Addition(val lhs: Expr, val rhs: Expr) : Expr()
        class Subtraction(val lhs: Expr, val rhs: Expr) : Expr()
        class Multiplication(val lhs: Expr, val rhs: Expr) : Expr()
        class Division(val lhs: Expr, val rhs: Expr) : Expr()
        class Modulus(val lhs: Expr, val rhs: Expr) : Expr()
    }
}