package org.wotopul

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.Test
import org.wotopul.Expr.Const
import org.wotopul.Expr.Variable
import org.wotopul.Statement.Write

class ParserTest {
    @Test
    fun test() {
        val input = """
            n := read();
            n := 1 + 3 * n;
            n := 1 + 3 * n;
            n := 1 + 3 * n;
            write(n)
        """
        val lexer = LanguageLexer(ANTLRInputStream(input))
        val parser = LanguageParser(CommonTokenStream(lexer))
        val program = parser.program()
        val actual = AbstractTreeBuilder().visit(program)
        val expected = programOf(sequence(
            read("n"),
            assignment("n", Const(1) + Const(3) * Variable("n")),
            assignment("n", Const(1) + Const(3) * Variable("n")),
            assignment("n", Const(1) + Const(3) * Variable("n")),
            Write(Variable("n"))
        ))
        // TODO is ';' left or right associative?
        // TODO rewrite Utils so that sequence does not insert Skip
    }
}