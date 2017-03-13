package org.wotopul

import org.wotopul.AbstractNode.LogicalExpr.ArithExpr
import org.wotopul.AbstractNode.LogicalExpr.ArithExpr.*
import org.wotopul.AbstractNode.Program
import org.wotopul.AbstractNode.Program.*

class AbstractTreeBuilder : LanguageBaseVisitor<AbstractNode>() {
    override fun visitSkip(ctx: LanguageParser.SkipContext?) = Skip

    override fun visitSequence(ctx: LanguageParser.SequenceContext?): AbstractNode {
        return Sequence(
            visit(ctx!!.first) as Program,
            visit(ctx.rest) as Program)
    }

    override fun visitWrite(ctx: LanguageParser.WriteContext?): AbstractNode {
        return Write(visit(ctx!!.expr()) as ArithExpr)
    }

    override fun visitAssignment(ctx: LanguageParser.AssignmentContext?): AbstractNode {
        val name = ctx!!.ID().text
        val expr = visit(ctx.expr()) as ArithExpr
        return Assignment(name, expr)
    }

    override fun visitConst(ctx: LanguageParser.ConstContext?): AbstractNode {
        val value = ctx!!.NUM().text.toInt()
        return ArithExpr.Const(value)
    }

    override fun visitParenthesis(ctx: LanguageParser.ParenthesisContext?): AbstractNode {
        return visit(ctx!!.getChild(0) /* there must be only one child */)
    }

    override fun visitInfix(ctx: LanguageParser.InfixContext?): AbstractNode {
        // TODO can we treat Kotlin constructor as a HOF?
        val lhs = visit(ctx!!.left) as ArithExpr
        val rhs = visit(ctx.right) as ArithExpr
        return when (ctx.op.type) {
            LanguageParser.OP_MUL -> Multiplication(lhs, rhs)
            LanguageParser.OP_DIV -> Division(lhs, rhs)
            LanguageParser.OP_ADD -> Addition(lhs, rhs)
            LanguageParser.OP_SUB -> Subtraction(lhs, rhs)
            else -> throw AssertionError()
        }
    }

    override fun visitVariable(ctx: LanguageParser.VariableContext?): AbstractNode {
        val name = ctx!!.ID().text
        return Variable(name)
    }

    override fun visitRead(ctx: LanguageParser.ReadContext?): AbstractNode {
        val name = ctx!!.ID().text
        return Read(name)
    }
}