package org.bashpile.core.bast

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*


class BashpileAstTest {
    @Test
    fun areAllStringLiterals() {
        val stringLiteral = StringLiteralBastNode("\"hello\"")
        val intLiteral = IntLiteralBastNode(1.toBigInteger())
        val booleanLiteral = BooleanLiteralBastNode(true)

        assertTrue(stringLiteral.areAllStringLiterals())
        assertFalse(intLiteral.areAllStringLiterals())
        assertFalse(booleanLiteral.areAllStringLiterals())

        val listOfStringLiterals = BashpileAst(listOf(stringLiteral, stringLiteral))
        assertTrue(listOfStringLiterals.areAllStringLiterals())

        val listWithIntAndString = BashpileAst(listOf(stringLiteral, intLiteral))
        assertFalse(listWithIntAndString.areAllStringLiterals())
    }
}