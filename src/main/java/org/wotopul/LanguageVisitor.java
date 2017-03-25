// Generated from Language.g4 by ANTLR 4.6
package org.wotopul;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link LanguageParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface LanguageVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link LanguageParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(LanguageParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by the {@code sequence}
	 * labeled alternative in {@link LanguageParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSequence(LanguageParser.SequenceContext ctx);
	/**
	 * Visit a parse tree produced by the {@code read}
	 * labeled alternative in {@link LanguageParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRead(LanguageParser.ReadContext ctx);
	/**
	 * Visit a parse tree produced by the {@code assignment}
	 * labeled alternative in {@link LanguageParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(LanguageParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code repeat}
	 * labeled alternative in {@link LanguageParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRepeat(LanguageParser.RepeatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code for}
	 * labeled alternative in {@link LanguageParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFor(LanguageParser.ForContext ctx);
	/**
	 * Visit a parse tree produced by the {@code skip}
	 * labeled alternative in {@link LanguageParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSkip(LanguageParser.SkipContext ctx);
	/**
	 * Visit a parse tree produced by the {@code while}
	 * labeled alternative in {@link LanguageParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhile(LanguageParser.WhileContext ctx);
	/**
	 * Visit a parse tree produced by the {@code write}
	 * labeled alternative in {@link LanguageParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWrite(LanguageParser.WriteContext ctx);
	/**
	 * Visit a parse tree produced by the {@code if}
	 * labeled alternative in {@link LanguageParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf(LanguageParser.IfContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#elif}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElif(LanguageParser.ElifContext ctx);
	/**
	 * Visit a parse tree produced by the {@code const}
	 * labeled alternative in {@link LanguageParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConst(LanguageParser.ConstContext ctx);
	/**
	 * Visit a parse tree produced by the {@code variable}
	 * labeled alternative in {@link LanguageParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(LanguageParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by the {@code infix}
	 * labeled alternative in {@link LanguageParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInfix(LanguageParser.InfixContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parenthesis}
	 * labeled alternative in {@link LanguageParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenthesis(LanguageParser.ParenthesisContext ctx);
}