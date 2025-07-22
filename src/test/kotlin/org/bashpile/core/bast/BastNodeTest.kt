package org.bashpile.core.bast

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bashpile.core.Main
import org.bashpile.core.SCRIPT_GENERIC_ERROR
import org.bashpile.core.bast.statements.PrintBastNode
import org.bashpile.core.bast.statements.ShellLineBastNode
import org.bashpile.core.bast.types.BooleanLiteralBastNode
import org.bashpile.core.bast.types.IntegerLiteralBastNode
import org.bashpile.core.bast.types.StringLiteralBastNode
import org.bashpile.core.bast.types.leaves.LeafBastNode
import org.bashpile.core.runCommand
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class BastNodeTest {
    val log: Logger = LogManager.getLogger()
    @Test
    fun areAllStrings() {
        val stringLiteral = StringLiteralBastNode("\"hello\"")
        val intLiteral = IntegerLiteralBastNode(1.toBigInteger())
        val booleanLiteral = BooleanLiteralBastNode(true)

        assertTrue(stringLiteral.areAllStrings())
        assertFalse(intLiteral.areAllStrings())
        assertFalse(booleanLiteral.areAllStrings())

        val listOfStringLiterals = InternalBastNode(listOf(stringLiteral, stringLiteral))
        assertTrue(listOfStringLiterals.areAllStrings())

        val listWithIntAndString = InternalBastNode(listOf(stringLiteral, intLiteral))
        assertFalse(listWithIntAndString.areAllStrings())
    }

    @Test
    fun deepCopyWorks() {
        val child = StringLiteralBastNode("Hello")
        val fixture = InternalBastNode(listOf(child))

        val origChild = fixture.children[0] as StringLiteralBastNode
        assertEquals(fixture.deepCopy().children.size, 1)
        val copyChild = fixture.deepCopy().children[0] as StringLiteralBastNode

        assertEquals(origChild.text, copyChild.text)
        assertNotEquals(origChild.toString(), copyChild.toString())
    }

    @Test
    fun mermaidGraphWorks() {
        val child = ShellLineBastNode()
        val root = PrintBastNode(listOf(child))
        val graph = root.mermaidGraph()
        assertFalse(graph.contains("reflection", true))
        assertFalse(graph.contains("BastNode"))
        log.info("Mermaid Graph: {}", graph)
    }

    /** Tests print(#(ls $(printf '.'; exit 1))) */
    @Test
    fun unnest_withPrint_works() {
        Main() // create for static state
        var printBastNode = PrintBastNode()
        var shellString = ShellStringBastNode()
        val ls = LeafBastNode("ls ")
        val subshell = ShellStringBastNode(listOf(LeafBastNode("echo '.'; exit 1")))
        shellString = shellString.replaceChildren(listOf(ls, subshell))
        printBastNode = printBastNode.replaceChildren(listOf(shellString))

        log.info("Mermaid Graph before unnest: {}", printBastNode.mermaidGraph())
        val unnestedRoot = printBastNode.unnestSubshells()
        log.info("Mermaid Graph after unnest: {}", unnestedRoot.mermaidGraph())
        assert(unnestedRoot.children.size == 2)
        assert(unnestedRoot.children[1].children.size == 1)

        val render = unnestedRoot.render()
        assertEquals("""
            declare __bp_var0
            __bp_var0="$(echo '.'; exit $SCRIPT_GENERIC_ERROR)"
            printf "$(ls ${'$'}{__bp_var0})"
            
        """.trimIndent(), render)
    }

    /** Tests set -euo pipefail; print(#(ls $(printf '.'; exit 1))) */
    @Test
    fun unnest_withPrint_strictMode_exitsWithScriptError() {
        Main() // create for static state

        // create printNode
        var printBastNode = PrintBastNode()
        var shellString = ShellStringBastNode()
        val ls = LeafBastNode("ls ")
        val subshell = ShellStringBastNode(listOf(LeafBastNode("echo '.'; exit 1")))
        shellString = shellString.replaceChildren(listOf(ls, subshell))
        printBastNode = printBastNode.replaceChildren(listOf(shellString))

        // create parent of printNode and sibling
        val strictNode = ShellLineBastNode(listOf(LeafBastNode("set -euo pipefail")))
        val root = InternalBastNode(listOf(strictNode, printBastNode))

        log.info("Mermaid Graph before unnest: {}", root.mermaidGraph())
        val unnestedRoot = root.unnestSubshells()
        log.info("Mermaid Graph after unnest: {}", unnestedRoot.mermaidGraph())
        assert(unnestedRoot.children.size == 2)
        assert(unnestedRoot.children[1].children.size == 2)

        val render = unnestedRoot.render()
        assertEquals("""
            set -euo pipefail
            declare __bp_var0
            __bp_var0="$(echo '.'; exit $SCRIPT_GENERIC_ERROR)"
            printf "$(ls ${'$'}{__bp_var0})"
            
        """.trimIndent(), render)

        assertEquals(SCRIPT_GENERIC_ERROR, render.runCommand().second)
    }
}
