package org.bashpile.core.bast.types

import org.bashpile.core.Main
import org.bashpile.core.bast.BastNode

class VariableReferenceBastNode(id: String, majorType: TypeEnum) : BastNode(mutableListOf(), id, majorType) {

    override fun render(): String {
        Main.bashpileState.assertInScope(id!!)
        return "${'$'}{$id}"
    }

    override fun replaceChildren(nextChildren: List<BastNode>): VariableReferenceBastNode {
        return VariableReferenceBastNode(id!!, majorType)
    }
}
