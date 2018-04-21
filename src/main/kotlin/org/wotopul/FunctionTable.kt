package org.wotopul

class FunctionTable(functionNames: Set<String>) {
    private val table: Array<String> = functionNames.toTypedArray()

    fun getName(index: Int) = table[index]

    fun getIndex(name: String) =
        table.indices.first { table[it] == name }
}
