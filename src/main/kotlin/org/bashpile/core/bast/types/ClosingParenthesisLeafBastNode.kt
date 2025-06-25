package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode

class ClosingParenthesisLeafBastNode : LeafBastNode(")") {
    override fun replaceChildren(nextChildren: List<BastNode>): LeafBastNode {
        return ClosingParenthesisLeafBastNode()
    }

    override fun render(): String {
        throw UnsupportedOperationException("Intermediate node")
    }
}