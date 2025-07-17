package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.RenderTuple

class VariableBastNode(id: String, majorType: TypeEnum) : BastNode(listOf(), id, majorType) {

    override fun render(): RenderTuple {
        return Pair(listOf(), "$$id")
    }

    override fun replaceChildren(nextChildren: List<BastNode>): VariableBastNode {
        return VariableBastNode(id!!, majorType)
    }
}
