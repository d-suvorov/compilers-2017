package org.wotopul

import org.wotopul.LogicalExpr.ArithExpr

sealed class Program {
    object Skip : Program()
    class Sequence(val first: Program, val rest: Program) : Program()
    class Assignment(val variable: String, val value: ArithExpr) : Program()
    class Read(val variable: String) : Program()
    class Write(val value: ArithExpr) : Program()
}

sealed class LogicalExpr {
    class And(val lhs: LogicalExpr, val rhs: LogicalExpr) : LogicalExpr()
    class Or(val lhs: LogicalExpr, val rhs: LogicalExpr) : LogicalExpr()

    class Less(val lhs: ArithExpr, val rhs: ArithExpr) : LogicalExpr()
    class Greater(val lhs: ArithExpr, val rhs: ArithExpr) : LogicalExpr()
    class Equal(val lhs: ArithExpr, val rhs: ArithExpr) : LogicalExpr()

    sealed class ArithExpr : LogicalExpr() {
        class Const(val value: Int) : ArithExpr()
        class Variable(val name: String) : ArithExpr()

        class Addition(val lhs: ArithExpr, val rhs: ArithExpr) : ArithExpr()
        class Subtraction(val lhs: ArithExpr, val rhs: ArithExpr) : ArithExpr()
        class Multiplication(val lhs: ArithExpr, val rhs: ArithExpr) : ArithExpr()
        class Division(val lhs: ArithExpr, val rhs: ArithExpr) : ArithExpr()
    }
}