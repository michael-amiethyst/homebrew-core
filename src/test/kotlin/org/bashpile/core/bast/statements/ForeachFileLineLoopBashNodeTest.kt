package org.bashpile.core.bast.statements

import org.bashpile.core.bast.types.TypeEnum.STRING
import org.bashpile.core.bast.types.VariableBastNode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ForeachFileLineLoopBashNodeTest {
    @Test
    fun render() {
        val child = PrintBastNode(VariableBastNode("col1", STRING))
        val fixture = ForeachFileLineLoopBashNode(child.toList(),"file.csv", "col1")
        assertEquals("""
            cat "file.csv" | while IFS=',' read -r col1; do
                printf "${'$'}{col1}"
            done

        """.trimIndent(), fixture.render())
    }

}