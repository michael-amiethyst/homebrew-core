package org.bashpile.core.bast.expressions.arithmetic

import org.bashpile.core.TypeEnum
import org.bashpile.core.bast.BastNode
import org.bashpile.core.engine.RenderOptions

class IntegerArithmeticBastNode(children: List<BastNode> = listOf())
    : ArithmeticBastNode(children, majorType = TypeEnum.INTEGER) {
    constructor(vararg child: BastNode) : this(child.toList())

    override fun render(options: RenderOptions): String {
        val childRenders = children.map { it.render(RenderOptions.Companion.UNQUOTED) }.joinToString(" ")
        return "$(($childRenders))"
    }

    override fun replaceChildren(nextChildren: List<BastNode>): IntegerArithmeticBastNode {
        return IntegerArithmeticBastNode(nextChildren.map { it.deepCopy() })
    }
}