package org.wotopul

import java.io.File

// TODO cut'n'paste in Main.kt

fun main(args: Array<String>) {
    val baseDir = "compiler-tests/"
    var success = true
    for (suite in arrayOf(/*"core", */ "expressions" /*, "deep-expressions"*/)) {
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
                }
                if (actual != expected) {
                    success = false
                    println("$mode mode: $suite : $case failed!")
                }
            }

            runMode("interpreter", ::runInterpreter)
            runMode("stack", ::runStackMachine)
        }
    }
    println(if (success) "All tests passed" else "There are errors!")
}

fun runInterpreter(program: Program, input: List<Int>): String =
    interpret(program, input)
        .map(Configuration.OutputItem::toString)
        .reduce(String::plus)

fun runStackMachine(program: Program, input: List<Int>): String =
    interpret(compile(program.main), input)
        .map(Configuration.OutputItem::toString)
        .reduce(String::plus)