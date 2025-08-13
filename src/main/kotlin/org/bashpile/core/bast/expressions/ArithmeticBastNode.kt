package org.bashpile.core.bast.expressions

import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.types.TypeEnum

class ArithmeticBastNode(children: List<BastNode> = listOf()) : BastNode(children, majorType = TypeEnum.INTEGER) {
    constructor(vararg child: BastNode) : this(child.toList())

    override fun render(): String {
        val childRenders = children.map { it.render() }.joinToString(" ")
        return "$(($childRenders))"
    }

    override fun replaceChildren(nextChildren: List<BastNode>): ArithmeticBastNode {
        return ArithmeticBastNode(nextChildren)
    }
}