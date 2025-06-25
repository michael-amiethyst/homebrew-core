package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode

class VariableBastNode(id: String, majorType: TypeEnum) : BastNode(listOf(), id, majorType) {

    override fun render(): String {
        return "$$id"
    }

    override fun replaceChildren(nextChildren: List<BastNode>): VariableBastNode {
        return VariableBastNode(id!!, majorType)
    }
}
