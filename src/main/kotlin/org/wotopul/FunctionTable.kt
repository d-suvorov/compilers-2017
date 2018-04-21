package org.wotopul

interface FunctionTable {
    fun functionDefinition(name: String): FunctionDefinition?
    fun getName(index: Int): String
    fun getIndex(name: String): Int
    fun isDeclared(name: String): Boolean
}
