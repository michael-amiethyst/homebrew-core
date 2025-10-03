package org.bashpile.core.bast.expressions.literals

import org.bashpile.core.TypeEnum
import org.bashpile.core.bast.BastNode

class ClosingParenthesisLeafBastNode : LeafBastNode(")", TypeEnum.STRING) {
    override fun replaceChildren(nextChildren: List<BastNode>): ClosingParenthesisLeafBastNode {
        return ClosingParenthesisLeafBastNode()
    }
}