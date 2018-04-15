package org.wotopul

import org.junit.Test
import java.io.File

// TODO cut'n'paste in Main.kt

class TestSuite {
    @Test
    fun runTests() {
        val baseDir = "compiler-tests/"
        var success = true
        for (suite in arrayOf("core", "expressions", "deep-expressions")) {
            print("Running tests in $suite")
            val testDir = "$baseDir/$suite"
            val list: List<String> = File(testDir).list().sorted()
                .filter({ it.endsWith(".expr") })
                .map({ it.substring(0, it.lastIndexOf(".")) })

            for (case in list) {
                val source = readFile("$testDir/$case.expr")
                val input = readIntegers(File("$testDir/$case.input").reader())
                val expected = readFile("$testDir/orig/$case.log")
                val program = parseProgram(source)

                fun runMode(mode: String, run: (Program, List<Int>) -> String) {
                    val actual = try {
                        run(program, input)
                    } catch (e: ExecutionException) {
                        success = false
                        println("$mode mode: $suite : $case failed: ${e.message}")
                        return
                    } catch (e: CompilationException) {
                        success = false
                        println("$mode mode: $suite : $case failed: compilation error")
                        return
                    }
                    if (actual != expected) {
                        success = false
                        println("$mode mode: $suite : $case failed!")
                    }
                }

                runMode("interpreter", ::runInterpreter)
                runMode("stack", ::runStackMachine)
                runMode("compiler") { p, _ -> runCompiler(testDir, case, p) }
            }
        }
        println(if (success) "All tests passed" else "There are errors!")
    }
}

fun runInterpreter(program: Program, input: List<Int>): String =
    interpret(program, input)
        .map(Configuration.OutputItem::toString)
        .fold("", String::plus)

fun runStackMachine(program: Program, input: List<Int>): String =
    interpret(compile(program), input)
        .map(Configuration.OutputItem::toString)
        .fold("", String::plus)

class CompilationException() : Exception()

fun runCompiler(testDir: String, name: String, program: Program): String {
    val fullName = "$testDir/$name"

    // Compile
    val stackProgram = compile(program)
    val asm = compile(stackProgram, program)
    val gccCode = invokeGCC(testDir, name, asm);
    if (gccCode != 0)
        throw CompilationException()

    // Run
    val testProc = Runtime.getRuntime()
        .exec(arrayOf(
            "/bin/sh",
            "-c",
            "cat $fullName.input | $fullName > $fullName.log"
        ))
    if (testProc.waitFor() != 0) {
        print(testProc.errorStream.reader().use { it.readText() })
        throw ExecutionException("program crashed")
    }

    // Read output
    val out = readFile("$fullName.log")

    val cleanProc = Runtime.getRuntime().exec(
        "rm $fullName.s $fullName $fullName.log"
    )
    if (cleanProc.waitFor() != 0) {
        print("An error occurred during clean up")
    }

    return out
}
