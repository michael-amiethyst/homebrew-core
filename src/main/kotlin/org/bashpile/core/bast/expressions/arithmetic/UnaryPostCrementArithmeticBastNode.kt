package org.bashpile.core.bast.expressions.arithmetic

import org.bashpile.core.TypeEnum
import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.expressions.ArithmeticBastNode
import org.bashpile.core.bast.expressions.literals.Literal

class UnaryPostCrementArithmeticBastNode(private val expressionNode: BastNode, private val operator: String
) : ArithmeticBastNode(
    mutableListOf(expressionNode),
    majorType = expressionNode.majorType())
{
    override fun render(): String {
        check (expressionNode.coercesTo(TypeEnum.INTEGER)) {
            "Post-increment/decrement can only be applied to integers, not ${expressionNode.majorType()}"
        }
        check (expressionNode !is Literal) { "Literals cannot be incremented or decremented" }
        val childRender = expressionNode.render()
        return "$((${childRender}$operator))"
    }

    override fun replaceChildren(nextChildren: List<BastNode>): UnaryPostCrementArithmeticBastNode {
        return UnaryPostCrementArithmeticBastNode(nextChildren[0].deepCopy(), operator)
    }
}