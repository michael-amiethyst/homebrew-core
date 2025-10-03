package org.bashpile.core.bast.expressions.literals

import org.bashpile.core.TypeEnum
import org.bashpile.core.bast.BastNode

/** May be true or false, not null */
class BooleanLiteralBastNode(private val bool: Boolean) : BastNode(mutableListOf(), majorType = TypeEnum.BOOLEAN) {
    override fun render(): String {
        return bool.toString()
    }

    override fun replaceChildren(nextChildren: List<BastNode>): BooleanLiteralBastNode {
        return BooleanLiteralBastNode(bool)
    }
}