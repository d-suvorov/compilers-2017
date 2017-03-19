package org.wotopul

import java.io.FileInputStream

fun main(args: Array<String>) {
    val usage = """
    |Usage: rc.native <mode> <source-file>
    |Available <mode> options:
    | -i  interpretation
    | -s  compilation to a stack machine representation and interpretation
    | -o  compilation
    | (See README here: https://github.com/anlun/compiler-tests.git)
    """.trimMargin()
    if (args.size != 2) {
        println(usage)
        System.exit(0)
    }
    when (args[0]) {
        "-i" -> {
            val source = readProgram(args[1])
            val program = parseProgram(source)
            val input = readInput()
            val output = interpret(program, input)
            if (output == null) println("Program crashed")
            else output.forEach(::print)
        }
        "-s" -> {
            val source = readProgram(args[1])
            val program = parseProgram(source)
            val stackProgram = compile(program)
            val input = readInput()
            val output = interpret(stackProgram, input)
            if (output == null) println("Program crashed")
            else output.forEach(::print)
        }
        "-o" -> {
            println("Compilation is not supported yet")
        }
        else -> {
            println("Unknown option: " + args[0])
            println(usage)
        }
    }
}

fun readProgram(filename: String) = FileInputStream(filename).reader().use { it.readText() }

fun readInput(): List<Int> = System.`in`.reader().use {
    it.readLines().map(String::toInt)
}