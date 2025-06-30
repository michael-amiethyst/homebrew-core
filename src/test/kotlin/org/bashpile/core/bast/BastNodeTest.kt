package org.bashpile.core.bast

import org.bashpile.core.bast.types.BooleanLiteralBastNode
import org.bashpile.core.bast.types.IntegerLiteralBastNode
import org.bashpile.core.bast.types.StringLiteralBastNode
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*


class BastNodeTest {
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
    fun simple_deepCopyWorks() {
        val child = StringLiteralBastNode("Hello")
        val fixture = InternalBastNode(listOf(child))

        val origChild = fixture.children[0] as StringLiteralBastNode
        assertEquals(fixture.deepCopy().children.size, 1)
        val copyChild = fixture.deepCopy().children[0] as StringLiteralBastNode

        assertEquals(origChild.text, copyChild.text)
        assertNotEquals(origChild.toString(), copyChild.toString())
    }

    // TODO unnest - make unnestSubshells tests, make double nested test
}
