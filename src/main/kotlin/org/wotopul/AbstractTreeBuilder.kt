package org.wotopul

import org.wotopul.Expr.*
import org.wotopul.Statement.*

class AbstractTreeBuilder : LanguageBaseVisitor<AbstractNode>() {
    override fun visitProgram(ctx: LanguageParser.ProgramContext?): Program {
        val functions = ctx!!.functionDefinition().map { visit(it) as FunctionDefinition }
        val main = visit(ctx.stmt()) as Statement
        return Program(functions, main)
    }

    override fun visitFunctionDefinition(
        ctx: LanguageParser.FunctionDefinitionContext?): FunctionDefinition
    {
        val name = ctx!!.ID().text
        val params = ctx.params().ID().map { it.text }
        val body = visit(ctx.stmt()) as Statement
        return FunctionDefinition(name, params, body, ctx.locals)
    }

    override fun visitSkip(ctx: LanguageParser.SkipContext?) = Skip

    override fun visitSequence(ctx: LanguageParser.SequenceContext?) =
        Sequence(
            visit(ctx!!.first) as Statement,
            visit(ctx.rest) as Statement)

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

    override fun visitVariable(ctx: LanguageParser.VariableContext?): Variable {
        val name = ctx!!.ID().text
        return Variable(name)
    }

    override fun visitParenthesis(ctx: LanguageParser.ParenthesisContext?): AbstractNode =
        // children by their indices must be:
        // 0 - open parenthesis, 1 - expression, 2 - close parenthesis
        visit(ctx!!.getChild(1))

    override fun visitInfix(ctx: LanguageParser.InfixContext?): Expr {
        val op = ctx!!.op.text
        val lhs = visit(ctx.left) as Expr
        val rhs = visit(ctx.right) as Expr
        return Binop(if (op == "!!") "||" else op, lhs, rhs)
    }

    override fun visitCharLiteral(ctx: LanguageParser.CharLiteralContext?): CharLiteral {
        val chStr = ctx!!.text.trim('\'')
        assert(chStr.length == 1)
        return CharLiteral(chStr.first())
    }

    override fun visitStringLiteral(ctx: LanguageParser.StringLiteralContext?): StringLiteral {
        val str = ctx!!.text.trim('\"')
        return StringLiteral(str)
    }

    override fun visitBooleanLiteral(ctx: LanguageParser.BooleanLiteralContext?): AbstractNode =
        when (ctx!!.text) {
            "false" -> Const(0)
            "true" -> Const(1)
            else -> throw AssertionError("unknown boolean value: ${ctx.text}")
        }

    override fun visitFunction(ctx: LanguageParser.FunctionContext?) =
        FunctionExpr(visitFunctionImpl(ctx!!.function_()))

    override fun visitRead(ctx: LanguageParser.ReadContext?): Read {
        val name = ctx!!.ID().text
        return Read(name)
    }

    override fun visitIf(ctx: LanguageParser.IfContext?): If {
        val elseClause = ctx!!.elseClause
        val initial = if (elseClause != null) visit(elseClause) as Statement else Skip
        val resultElseClause = ctx.elif().reversed().fold(initial, fun(curr, elif): Statement {
            return If(visit(elif.cond) as Expr, visit(elif.elifClause) as Statement, curr)
        })
        return If(visit(ctx.cond) as Expr, visit(ctx.thenClause) as Statement, resultElseClause)
    }

    override fun visitWhile(ctx: LanguageParser.WhileContext?): While {
        val cond = visit(ctx!!.cond) as Expr
        val body = visit(ctx.body) as Statement
        return While(cond, body)
    }

    override fun visitRepeat(ctx: LanguageParser.RepeatContext?): Repeat {
        val body = visit(ctx!!.body) as Statement
        val cond = visit(ctx.cond) as Expr
        return Repeat(body, cond)
    }

    override fun visitFor(ctx: LanguageParser.ForContext?) =
        Sequence(
            visit(ctx!!.init) as Statement,
            While(
                visit(ctx.cond) as Expr,
                Sequence(
                    visit(ctx.body) as Statement,
                    visit(ctx.after) as Statement
                )
            )
        )

    override fun visitReturnStatement(ctx: LanguageParser.ReturnStatementContext?): Return {
        val value = visit(ctx!!.expr()) as Expr
        return Return(value)
    }

    override fun visitFunctionStatement(ctx: LanguageParser.FunctionStatementContext?) =
        FunctionStatement(visitFunctionImpl(ctx!!.function_()))

    fun visitFunctionImpl(function: LanguageParser.Function_Context): FunctionCall {
        val name = function.ID().text
        val args = function.args().expr().map { visit(it) as Expr }
        return FunctionCall(name, args)
    }
}