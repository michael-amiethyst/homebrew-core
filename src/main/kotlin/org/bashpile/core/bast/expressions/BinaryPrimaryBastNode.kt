package org.bashpile.core.bast.expressions

import org.bashpile.core.TypeEnum
import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.expressions.literals.TerminalBastNode

class BinaryPrimaryBastNode(val left: BastNode, val operator: String, val right: BastNode)
    : BastNode(mutableListOf(TerminalBastNode(operator, TypeEnum.STRING)))
{
    override fun replaceChildren(nextChildren: List<BastNode>): BastNode {
        return BinaryPrimaryBastNode(left, operator, right)
    }

    override fun render(): String {
        val numeric = left.majorType().coercesTo(TypeEnum.INTEGER) && right.majorType().coercesTo(TypeEnum.INTEGER)
        val strings = left.majorType().coercesTo(TypeEnum.STRING) && right.majorType().coercesTo(TypeEnum.STRING)
        require(numeric || strings) { "Mismatched types: ${left.majorType()} and ${right.majorType()}" }

        val leftRender = if (left is VariableReferenceBastNode) "\"${left.render()}\"" else left.render()
        val rightRender = if (right is VariableReferenceBastNode)"\"${right.render()}\"" else right.render()

        val translatedOperator = if (strings) {
            operator
        } else {
            when(operator) {
                ">" -> "-gt"
                ">=" -> "-ge"
                "<" -> "-lt"
                "<=" -> "-le"
                "=" -> "-eq"
                "!=" -> "-ne"
                else -> throw IllegalStateException("Unknown operator: $operator")
            }
        }
        return "[ $leftRender $translatedOperator $rightRender ]"
    }
}
