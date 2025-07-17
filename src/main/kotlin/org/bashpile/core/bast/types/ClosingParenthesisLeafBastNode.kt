package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.RenderTuple

class ClosingParenthesisLeafBastNode : LeafBastNode(")") {
    override fun replaceChildren(nextChildren: List<BastNode>): LeafBastNode {
        return ClosingParenthesisLeafBastNode()
    }

    override fun render(): RenderTuple {
        throw UnsupportedOperationException("Intermediate node")
    }
}