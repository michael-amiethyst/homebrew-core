package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode

open class LeafBastNode(private val text: String) : BastNode(listOf()) {
    override fun render(): Pair<List<BastNode>, String> {
        return Pair(listOf(), text)
    }

    override fun replaceChildren(nextChildren: List<BastNode>): LeafBastNode {
        return LeafBastNode(text)
    }
}
