package org.bashpile.core.bast

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bashpile.core.Main
import org.bashpile.core.bast.types.BooleanLiteralBastNode
import org.bashpile.core.bast.types.IntegerLiteralBastNode
import org.bashpile.core.bast.types.StringLiteralBastNode
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*


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

    @Test
    fun unnest_withPrint_works() {
        // TODO unnest -- impl
        Main() // create for static state
        var root = PrintBastNode()
        var shellString = ShellStringBastNode()
        val ls = StringLiteralBastNode("ls")
        val subshell = ShellStringBastNode(listOf(StringLiteralBastNode("printf '.'; exit 1")))
        shellString = shellString.replaceChildren(listOf(ls, subshell))
        root = root.replaceChildren(listOf(shellString))
        log.info("Mermaid Graph before unnest: {}", root.mermaidGraph())
        val unnestedRoot = root.unnestSubshells()
        log.info("Mermaid Graph after unnest: {}", unnestedRoot.mermaidGraph())
        assert(unnestedRoot.children.size == 2)
        assert(unnestedRoot.children[1].children.size == 2)

        val render = unnestedRoot.render()
        assertEquals("""
            declare __bp_var2
            __bp_var2="$(printf '.'; exit 1)"
            ls ${'$'}__bp_var2
        """.trimIndent(), render)
    }

    // TODO unnest - make double nested test
}
