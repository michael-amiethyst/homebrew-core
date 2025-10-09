package org.bashpile.core.bast.expressions

import org.bashpile.core.Main.Companion.callStack
import org.bashpile.core.Subshell
import org.bashpile.core.TypeEnum
import org.bashpile.core.bast.BastNode

class VariableReferenceBastNode(id: String, majorType: TypeEnum) : BastNode(mutableListOf(), id, majorType) {

    override fun render(): String {
        callStack.requireOnStack(id!!)
        return if (isBashVariableReference()) { "${'$'}{$id}" } else { id }
    }

    override fun replaceChildren(nextChildren: List<BastNode>): VariableReferenceBastNode {
        return VariableReferenceBastNode(id!!, majorType())
    }

    /** If not then we don't need a preceding '$' (e.g. we are in $(()) braces) */
    private fun isBashVariableReference(): Boolean {
        val arithmeticNode = parents().find { it is ArithmeticBastNode }
        return arithmeticNode == null || arithmeticNode is Subshell
    }
}
