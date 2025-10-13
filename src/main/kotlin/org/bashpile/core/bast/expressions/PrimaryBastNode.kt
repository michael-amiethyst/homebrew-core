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
        // TODO check if this is needed 'is CombiningExpressionBastNode'
        return if (this is VariableReferenceBastNode || this is StringLiteralBastNode || this is UnaryPrimaryBastNode ||
                this is BinaryPrimaryBastNode) {
            "\"${this.render()}\""
        } else { this.render() }
    }
}
