package org.wotopul

import org.wotopul.AbstractNode.Expr
import org.wotopul.AbstractNode.Expr.*
import org.wotopul.AbstractNode.Program
import org.wotopul.AbstractNode.Program.*

class AbstractTreeBuilder : LanguageBaseVisitor<AbstractNode>() {
    override fun visitSkip(ctx: LanguageParser.SkipContext?) = Skip

    override fun visitSequence(ctx: LanguageParser.SequenceContext?) =
        Sequence(
            visit(ctx!!.first) as Program,
            visit(ctx.rest) as Program)

    override fun visitWrite(ctx: LanguageParser.WriteContext?) =
        Write(visit(ctx!!.expr()) as Expr)

    override fun visitAssignment(ctx: LanguageParser.AssignmentContext?): Assignment {
        val name = ctx!!.ID().text
        val expr = visit(ctx.expr()) as Expr
        return Assignment(name, expr)
    }

    override fun visitConst(ctx: LanguageParser.ConstContext?): Const {
        val value = ctx!!.NUM().text.toInt()
        return Const(value)
    }

    override fun visitParenthesis(ctx: LanguageParser.ParenthesisContext?): AbstractNode =
        // children by their indices must be:
        // 0 - open parenthesis, 1 - expression, 2 - close parenthesis
        visit(ctx!!.getChild(1))

    override fun visitInfix(ctx: LanguageParser.InfixContext?): Expr {
        val op = ctx!!.op.text
        val lhs = visit(ctx.left) as Expr
        val rhs = visit(ctx.right) as Expr
        return Binop(op, lhs, rhs)
    }

    override fun visitVariable(ctx: LanguageParser.VariableContext?): Variable {
        val name = ctx!!.ID().text
        return Variable(name)
    }

    override fun visitRead(ctx: LanguageParser.ReadContext?): Read {
        val name = ctx!!.ID().text
        return Read(name)
    }
}