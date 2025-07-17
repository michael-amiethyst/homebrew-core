package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.RenderTuple

/** May be true or false, not null */
class BooleanLiteralBastNode(private val bool: Boolean) : BastNode(listOf(), majorType = TypeEnum.BOOLEAN) {
    override fun render(): RenderTuple {
        return Pair(listOf(), bool.toString())
    }

    override fun replaceChildren(nextChildren: List<BastNode>): BooleanLiteralBastNode {
        return BooleanLiteralBastNode(bool)
    }
}
