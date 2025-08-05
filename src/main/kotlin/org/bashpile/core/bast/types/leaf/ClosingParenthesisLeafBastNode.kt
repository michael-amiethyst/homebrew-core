package org.bashpile.core.bast.types.leaf

import org.bashpile.core.bast.BastNode

class ClosingParenthesisLeafBastNode : LeafBastNode(CLOSING_PARENTHESIS) {
    companion object {
        @JvmStatic
        val CLOSING_PARENTHESIS = ")"
    }
    override fun replaceChildren(nextChildren: List<BastNode>): ClosingParenthesisLeafBastNode {
        return ClosingParenthesisLeafBastNode()
    }
}