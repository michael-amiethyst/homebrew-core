package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.RenderTuple

open class LeafBastNode(private val text: String) : BastNode(listOf()) {
    override fun render(): RenderTuple {
        return Pair(listOf(), text)
    }

    override fun replaceChildren(nextChildren: List<BastNode>): LeafBastNode {
        return LeafBastNode(text)
    }
}
