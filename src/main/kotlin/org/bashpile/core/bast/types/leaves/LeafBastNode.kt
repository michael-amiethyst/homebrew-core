package org.bashpile.core.bast.types.leaves

import org.bashpile.core.bast.BastNode

open class LeafBastNode(private val text: String) : BastNode(listOf()) {
    override fun render(): String {
        return text
    }

    override fun replaceChildren(nextChildren: List<BastNode>): BastNode {
        return LeafBastNode(text)
    }
}