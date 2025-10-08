package org.bashpile.core.bast.expressions

import org.bashpile.core.TypeEnum
import org.bashpile.core.bast.BastNode

class UnaryPrimaryBastNode(private val operator: String, private val rightExpression: BastNode)
    : BastNode(mutableListOf(rightExpression), majorType = TypeEnum.BOOLEAN) {

    override fun render(): String {
        val rightRendered = if (rightExpression is VariableReferenceBastNode) {
            "\"${rightExpression.render()}\""
        } else { rightExpression.render() }
        val bashOperator = when (operator) {
            "isEmpty" -> "-z"
            "isNotEmpty" -> "-n"
            else -> throw IllegalStateException("Unknown unary operator: $operator")
        }
        return "[ $bashOperator $rightRendered ]"
    }

    override fun replaceChildren(nextChildren: List<BastNode>): BastNode {
        return UnaryPrimaryBastNode(operator, rightExpression.deepCopy())
    }
}
