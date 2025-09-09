package org.bashpile.core.bast.types.leaf

import org.bashpile.core.bast.BastNode

open class LeafBastNode(private val text: String) : BastNode(mutableListOf()) {
    override fun render(): String {
        return text
    }

    override fun replaceChildren(nextChildren: List<BastNode>): LeafBastNode {
        return LeafBastNode(text)
    }
}
