package org.bashpile.core.bast.expressions

import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.Subshell
import org.bashpile.core.bast.types.TypeEnum

class FloatArithmeticBastNode(children: List<BastNode> = listOf())
    : ArithmeticBastNode(children, majorType = TypeEnum.FLOAT), Subshell {

    constructor(vararg child: BastNode) : this(child.toList())

    override fun render(): String {
        val childRenders = children.map { it.render() }.joinToString(" ")
        return "$(bc <<< \"$childRenders\")"
    }

    override fun replaceChildren(nextChildren: List<BastNode>): FloatArithmeticBastNode {
        return FloatArithmeticBastNode(nextChildren)
    }
}
