package org.wotopul

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.FileOutputStream

fun parseProgram(input: String): Program {
    val lexer = LanguageLexer(ANTLRInputStream(input))
    val parser = LanguageParser(CommonTokenStream(lexer))
    val program = parser.program()
    return AbstractTreeBuilder().visit(program) as Program
}

fun invokeGCC(dir: String, name: String, asm: String): Int {
    // write generated asm to a file
    val asmFilename = "$dir/$name.s" // TODO no extension in original filename
    FileOutputStream(asmFilename).bufferedWriter().use { it.write(asm) }

    // invoke assembler (GCC)
    val rcRuntime = System.getenv("RC_RUNTIME") ?: "./runtime"
    val gccProc = Runtime.getRuntime()
        .exec("gcc -g -m32 -o $dir/$name $rcRuntime/runtime.o $asmFilename")
    if (gccProc.waitFor() != 0) {
        print(gccProc.errorStream.reader().use { it.readText() })
    }
    return gccProc.exitValue()
}