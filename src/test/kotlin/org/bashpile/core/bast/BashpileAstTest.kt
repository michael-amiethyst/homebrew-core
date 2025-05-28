package org.bashpile.core.bast

import org.bashpile.core.bast.types.BooleanLiteralBastNode
import org.bashpile.core.bast.types.IntLiteralBastNode
import org.bashpile.core.bast.types.StringLiteralBastNode
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

        val listOfStringLiterals = InternalBastNode(listOf(stringLiteral, stringLiteral))
        assertTrue(listOfStringLiterals.areAllStrings())

        val listWithIntAndString = InternalBastNode(listOf(stringLiteral, intLiteral))
        assertFalse(listWithIntAndString.areAllStrings())
    }
}