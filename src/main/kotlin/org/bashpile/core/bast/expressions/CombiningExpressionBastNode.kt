package org.bashpile.core.bast.expressions

import org.bashpile.core.TypeEnum
import org.bashpile.core.bast.BastNode

class CombiningExpressionBastNode(private val left: BastNode, private val operator: String, private val right: BastNode)
    : BastNode(mutableListOf(left, right), majorType = TypeEnum.BOOLEAN)
{
    override fun render(): String {
        val bashOperator = when(operator) {
            "and" -> "&&"
            "or" -> "||"
            else -> throw IllegalArgumentException("Unknown combining operator: $operator")
        }
        return "${left.render()} $bashOperator ${right.render()}"
    }

    override fun replaceChildren(nextChildren: List<BastNode>): BastNode {
        return CombiningExpressionBastNode(nextChildren[0].deepCopy(), operator, nextChildren[1].deepCopy())
    }
}
