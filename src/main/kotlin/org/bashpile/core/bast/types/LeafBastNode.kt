package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode

open class LeafBastNode(private val text: String) : BastNode(listOf()) {
    override fun render(): String {
        return text
    }

    override fun deepCopy(): LeafBastNode {
        return LeafBastNode(text)
    }
}
