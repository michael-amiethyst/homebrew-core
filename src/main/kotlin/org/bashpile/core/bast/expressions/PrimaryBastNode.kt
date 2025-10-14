package org.bashpile.core.bast.expressions

import org.bashpile.core.TypeEnum
import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.expressions.literals.StringLiteralBastNode

/** See also [UnaryPrimaryBastNode] */
abstract class PrimaryBastNode(left: BastNode?, protected val operator: String, right: BastNode)
    : BastNode(if (left != null) {
    mutableListOf(left, right)
    } else { mutableListOf(right) }, majorType = TypeEnum.BOOLEAN)
{
    /** Render (and quote if needed) */
    protected fun BastNode.renderAndQuoteAsNeeded(): String {
        val isStringy = this is StringLiteralBastNode || this is ShellStringBastNode
        return if (isStringy || this is VariableReferenceBastNode || this is PrimaryBastNode) {
            "\"${this.render()}\""
        } else { this.render() }
    }
}
