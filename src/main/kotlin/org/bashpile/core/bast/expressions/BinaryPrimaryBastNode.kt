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
        val translatedOperator = if (strings) {
            operator
        } else {
            when(operator) {
                ">" -> "-gt"
                ">=" -> "-gte"
                "<" -> "-lt"
                "<=" -> "-lte"
                "=" -> "-eq"
                else -> throw IllegalStateException("Unknown operator: $operator")
            }
        }
        return "${left.render()} $translatedOperator ${right.render()}"
    }
}