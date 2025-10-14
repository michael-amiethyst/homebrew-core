package org.bashpile.core.bast.statements

import org.bashpile.core.bast.statements.ForeachFileLineLoopBashNode.Companion.sed
import org.bashpile.core.TypeEnum
import org.bashpile.core.TypeEnum.STRING
import org.bashpile.core.bast.expressions.VariableReferenceBastNode
import org.bashpile.core.bast.expressions.literals.TerminalBastNode
import org.bashpile.core.engine.RenderOptions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ForeachFileLineLoopBashNodeTest {
    @Test
    fun render_withPrint_works() {
        val child = PrintBastNode(VariableReferenceBastNode("col1", STRING))
        val fixture = ForeachFileLineLoopBashNode(
            child.asList(),"\"file.csv\"", listOf(VariableReferenceBastNode("col1", STRING)))
        assertEquals(
            """
            cat "file.csv" | $sed -e '1d' -e 's/\r//g' | $sed -ze '/\n$/!s/$/\n$/g' | while IFS=',' read -r col1; do
                printf "${'$'}{col1}"
            done

        """.trimIndent(), fixture.render(RenderOptions.UNQUOTED))
    }

    /** Statement in block needs 2 Bash lines to render */
    @Test
    fun render_withVariableDeclaration_works() {
        val child = VariableDeclarationBastNode(
            "col1", STRING, TypeEnum.EMPTY, child = TerminalBastNode("exampleValue", STRING))
        val fixture = ForeachFileLineLoopBashNode(
            child.asList(),"\"file.csv\"", listOf(VariableReferenceBastNode("col1", STRING)))
        assertEquals(
            """
            cat "file.csv" | $sed -e '1d' -e 's/\r//g' | $sed -ze '/\n$/!s/$/\n$/g' | while IFS=',' read -r col1; do
                declare col1
                col1="exampleValue"
            done

        """.trimIndent(), fixture.render(RenderOptions.UNQUOTED))
    }
}
