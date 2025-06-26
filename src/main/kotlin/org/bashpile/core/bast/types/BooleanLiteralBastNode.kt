package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode

/** May be true or false, not null */
class BooleanLiteralBastNode(private val bool: Boolean) : BastNode(listOf(), majorType = TypeEnum.BOOLEAN) {
    override fun render(): Pair<List<BastNode>, String> {
        return Pair(listOf(), bool.toString())
    }

    override fun replaceChildren(nextChildren: List<BastNode>): BooleanLiteralBastNode {
        return BooleanLiteralBastNode(bool)
    }
}
