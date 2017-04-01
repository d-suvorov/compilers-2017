package org.wotopul

import java.io.FileInputStream
import java.io.InputStreamReader

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
            try {
                val source = readFile(args[1])
                val program = parseProgram(source)
                val input = readInput()
                val output = interpret(program, input)
                output.forEach(::print)
            } catch (e: ExecutionException) {
                println("Program crashed: ${e.message}")
            }
        }
        "-s" -> {
            try {
                val source = readFile(args[1])
                val program = parseProgram(source)
                val stackProgram = compile(program.main)
                val input = readInput()
                val output = interpret(stackProgram, input)
                output.forEach(::print)
            } catch (e: ExecutionException) {
                println("Program crashed: ${e.message}")
            }
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

fun readFile(filename: String) = FileInputStream(filename).reader().use { it.readText() }

fun readInput(): List<Int> = readIntegers(System.`in`.reader())

fun readIntegers(reader: InputStreamReader) = reader.use { it.readLines().map(String::toInt) }