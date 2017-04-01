package org.wotopul

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream

fun parseProgram(input: String): Program {
    val lexer = LanguageLexer(ANTLRInputStream(input))
    val parser = LanguageParser(CommonTokenStream(lexer))
    val program = parser.program()
    return AbstractTreeBuilder().visit(program) as Program
}
