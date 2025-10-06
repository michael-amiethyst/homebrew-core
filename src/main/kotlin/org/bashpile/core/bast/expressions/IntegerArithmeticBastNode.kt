package org.bashpile.core.bast.expressions

import org.bashpile.core.bast.BastNode
import org.bashpile.core.TypeEnum

class IntegerArithmeticBastNode(children: List<BastNode> = listOf())
    : ArithmeticBastNode(children, majorType = TypeEnum.INTEGER) {
    constructor(vararg child: BastNode) : this(child.toList())

    override fun render(): String {
        val childRenders = children.map { it.render() }.joinToString(" ")
        return "$(($childRenders))"
    }

    override fun replaceChildren(nextChildren: List<BastNode>): IntegerArithmeticBastNode {
        return IntegerArithmeticBastNode(nextChildren.map { it.deepCopy() })
    }
}
