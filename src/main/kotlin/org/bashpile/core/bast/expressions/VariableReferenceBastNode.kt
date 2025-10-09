package org.bashpile.core.bast.expressions

import org.bashpile.core.Main.Companion.callStack
import org.bashpile.core.Subshell
import org.bashpile.core.TypeEnum
import org.bashpile.core.bast.BastNode

class VariableReferenceBastNode(id: String, majorType: TypeEnum) : BastNode(mutableListOf(), id, majorType) {

    override fun render(): String {
        callStack.requireOnStack(id!!)
        // TODO if subshell before arithimeticBastNode it will bug out, create test to verify
        val arithmeticNode = parents().find { it is ArithmeticBastNode }
        return if (arithmeticNode == null || arithmeticNode is Subshell) { "${'$'}{$id}" } else {
            // builtin, no '$' required
            id
        }
    }

    override fun replaceChildren(nextChildren: List<BastNode>): VariableReferenceBastNode {
        return VariableReferenceBastNode(id!!, majorType())
    }
}
