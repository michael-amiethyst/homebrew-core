package org.bashpile.core.bast.statements

import org.bashpile.core.bast.types.TypeEnum
import org.bashpile.core.bast.types.TypeEnum.STRING
import org.bashpile.core.bast.types.VariableBastNode
import org.bashpile.core.bast.types.leaf.LeafBastNode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ForeachFileLineLoopBashNodeTest {
    @Test
    fun render_withPrint_works() {
        val child = PrintBastNode(VariableBastNode("col1", STRING))
        val fixture = ForeachFileLineLoopBashNode(
            child.toList(),"\"file.csv\"", listOf(VariableBastNode("col1", STRING)))
        assertEquals("""
            cat "file.csv" | while IFS=',' read -r col1; do
                printf "${'$'}{col1}"
            done

        """.trimIndent(), fixture.render())
    }

    /** Statement in block needs 2 Bash lines to render */
    @Test
    fun render_withVariableDeclaration_works() {
        val child = VariableDeclarationBastNode(
            "col1", STRING, TypeEnum.EMPTY, child = LeafBastNode("exampleValue"))
        val fixture = ForeachFileLineLoopBashNode(
            child.toList(),"\"file.csv\"", listOf(VariableBastNode("col1", STRING)))
        assertEquals("""
            cat "file.csv" | while IFS=',' read -r col1; do
                declare col1
                col1="exampleValue"
            done

        """.trimIndent(), fixture.render())
    }

}