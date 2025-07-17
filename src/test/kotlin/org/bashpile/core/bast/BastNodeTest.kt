package org.bashpile.core.bast

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
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
        var root = PrintBastNode()
    }

    // TODO unnest - make unnestSubshells tests, make double nested test
}
