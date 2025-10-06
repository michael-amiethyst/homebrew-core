package org.bashpile.core.bast.expressions

import org.bashpile.core.Main.Companion.callStack
import org.bashpile.core.TypeEnum
import org.bashpile.core.bast.BastNode

class VariableReferenceBastNode(id: String, majorType: TypeEnum) : BastNode(mutableListOf(), id, majorType) {

    override fun render(): String {
        callStack.requireOnStack(id!!)
        return "${'$'}{$id}"
    }

    override fun replaceChildren(nextChildren: List<BastNode>): VariableReferenceBastNode {
        return VariableReferenceBastNode(id!!, majorType())
    }
}
