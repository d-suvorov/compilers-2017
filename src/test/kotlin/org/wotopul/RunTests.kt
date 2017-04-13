package org.wotopul

import java.io.File

fun main(args: Array<String>) {
    val baseDir = "compiler-tests/"
    var success = true
    for (suite in arrayOf(/*"core", */ "core" /*, "deep-expressions"*/)) {
        val testDir = "$baseDir/$suite"
        val list: List<String> = File(testDir).list().sorted()
            .filter({ it.endsWith(".expr") })
            .map({ it.substring(0, it.lastIndexOf(".")) })
        for (case in list) {
            val source = readFile("$testDir/$case.expr")
            val input = readIntegers(File("$testDir/$case.input").reader())
            val expected = readFile("$testDir/orig/$case.log")
            val program = parseProgram(source)
            val actual = try {
                interpret(program, input)
                    .map(Configuration.OutputItem::toString)
                    .reduce(String::plus)
            } catch (e: ExecutionException) {
                success = false
                println("$suite : $case failed: ${e.message}")
                continue
            }
            if (actual != expected) {
                success = false
                println("$suite : $case failed!")
            }
        }
    }
    println(if (success) "All tests passed" else "There are errors!")
}