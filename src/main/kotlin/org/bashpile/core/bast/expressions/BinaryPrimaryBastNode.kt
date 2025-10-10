package org.bashpile.core.bast.expressions

import org.bashpile.core.TypeEnum
import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.expressions.literals.StringLiteralBastNode

class BinaryPrimaryBastNode(val left: BastNode, val operator: String, val right: BastNode)
    : BastNode(mutableListOf(left, right), majorType = TypeEnum.BOOLEAN)
{
    override fun replaceChildren(nextChildren: List<BastNode>): BastNode {
        return BinaryPrimaryBastNode(left, operator, right)
    }

    override fun render(): String {
        val integers = left.majorType().coercesTo(TypeEnum.INTEGER) && right.majorType().coercesTo(TypeEnum.INTEGER)
        val floats = left.majorType().coercesTo(TypeEnum.FLOAT) && right.majorType().coercesTo(TypeEnum.FLOAT)
        val strings = left.majorType().coercesTo(TypeEnum.STRING) && right.majorType().coercesTo(TypeEnum.STRING)
        require(integers || floats || strings) { "Mismatched types: ${left.majorType()} and ${right.majorType()}" }

        val translatedOperator = if (!integers) {
            operator
        } else {
            when(operator) {
                ">" -> "-gt"
                ">=" -> "-ge"
                "<" -> "-lt"
                "<=" -> "-le"
                "==" -> "-eq"
                "!=" -> "-ne"
                else -> throw IllegalStateException("Unknown operator: $operator")
            }
        }
        return if (integers || strings) {
            val leftRender = left.renderAndQuoteAsNeeded()
            val rightRender = right.renderAndQuoteAsNeeded()
            "[ $leftRender $translatedOperator $rightRender ]"
        } else {
            // floats
            "bc -l <<< \"${left.render()} $translatedOperator ${right.render()}\" > /dev/null"
        }
    }

    /** Render (and quote if needed) */
    private fun BastNode.renderAndQuoteAsNeeded(): String {
        return if (this is VariableReferenceBastNode || this is StringLiteralBastNode) {
            "\"${this.render()}\""
        } else { this.render() }
    }
}
