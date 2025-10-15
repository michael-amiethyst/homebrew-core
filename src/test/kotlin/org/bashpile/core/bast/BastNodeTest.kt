package org.bashpile.core.bast

import org.bashpile.core.bast.expressions.literals.StringLiteralBastNode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


class BastNodeTest {

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
}
