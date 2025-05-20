package org.bashpile.core.bast

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*


class BashpileAstTest {
    @Test
    fun areAllStrings() {
        val stringLiteral = StringLiteralBastNode("\"hello\"")
        val intLiteral = IntLiteralBastNode(1.toBigInteger())
        val booleanLiteral = BooleanLiteralBastNode(true)

        assertTrue(stringLiteral.areAllStrings())
        assertFalse(intLiteral.areAllStrings())
        assertFalse(booleanLiteral.areAllStrings())

        val listOfStringLiterals = BastNode(listOf(stringLiteral, stringLiteral))
        assertTrue(listOfStringLiterals.areAllStrings())

        val listWithIntAndString = BastNode(listOf(stringLiteral, intLiteral))
        assertFalse(listWithIntAndString.areAllStrings())
    }
}