package org.bashpile.core.bast.expressions

import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.types.TypeEnum

class ParenthesisBastNode(children: List<BastNode> = listOf(), majorType: TypeEnum = TypeEnum.UNKNOWN)
    : BastNode(children.toMutableList(), majorType = majorType) {

    override fun render(): String {
        val childRenders = children.map { it.render() }.joinToString(" ")
        return if (allParents().any { it is ArithmeticBastNode }) {
            "($childRenders)"
        } else {
            childRenders
        }
    }

    override fun replaceChildren(nextChildren: List<BastNode>): ParenthesisBastNode {
        return ParenthesisBastNode(nextChildren.map { it.deepCopy() })
    }
}
