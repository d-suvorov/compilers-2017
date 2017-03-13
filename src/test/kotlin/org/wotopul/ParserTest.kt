package org.wotopul

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Test
import org.wotopul.AbstractNode.LogicalExpr.ArithExpr.Const
import org.wotopul.AbstractNode.LogicalExpr.ArithExpr.Variable
import org.wotopul.AbstractNode.Program.*

class ParserTest {
    @Test
    fun test() {
        val input = """
            read(n);
            n := 1 + 3 * n;
            n := 1 + 3 * n;
            n := 1 + 3 * n;
            write(n)
        """
        val lexer = LanguageLexer(ANTLRInputStream(input))
        val parser = LanguageParser(CommonTokenStream(lexer))
        val program = parser.program()
        val actual = AbstractTreeBuilder().visit(program)
        val expected = sequence(
            Read("n"),
            Assignment("n", Const(1) + Const(3) * Variable("n")),
            Assignment("n", Const(1) + Const(3) * Variable("n")),
            Assignment("n", Const(1) + Const(3) * Variable("n")),
            Write(Variable("n"))
        )
        // TODO is ';' left or right associative?
        // TODO rewrite Utils so that sequence does not insert Skip
    }
}