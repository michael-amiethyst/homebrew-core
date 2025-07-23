package org.bashpile.core.bast.types.leaves

import org.bashpile.core.bast.BastNode

class ClosingParenthesisLeafBastNode : LeafBastNode(")") {
    override fun replaceChildren(nextChildren: List<BastNode>): BastNode {
        return ClosingParenthesisLeafBastNode()
    }

    override fun render(): String {
        throw UnsupportedOperationException("Intermediate node")
    }
}