package org.bashpile.core.bast

import org.bashpile.core.bast.types.StringLiteralBastNode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class InternalBastNodeTest {
    @Test
    fun simple_deepCopyWorks() {
        val child = StringLiteralBastNode("Hello")
        val fixture = InternalBastNode(listOf(child))

        val origChild = fixture.children[0] as StringLiteralBastNode
        assertEquals(fixture.deepCopy().children.size, 1)
        val copyChild = fixture.deepCopy().children[0] as StringLiteralBastNode

        assertEquals(origChild.text, copyChild.text)
    }

    // TODO unnest - make more deepCopy tests (e.g. with children, grandchildren)

    // TODO unnest - make unnestSubshells tests, make double nested test

}