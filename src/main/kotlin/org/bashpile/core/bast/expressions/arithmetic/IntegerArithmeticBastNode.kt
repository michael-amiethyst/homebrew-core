package org.bashpile.core.bast.expressions.arithmetic

import org.bashpile.core.TypeEnum.INTEGER
import org.bashpile.core.bast.BastNode
import org.bashpile.core.engine.RenderOptions
import org.bashpile.core.engine.RenderOptions.Companion.INTEGER_ARITHMETIC

class IntegerArithmeticBastNode(children: List<BastNode> = listOf())
    : ArithmeticBastNode(children, majorType = INTEGER)
{
    constructor(vararg child: BastNode) : this(child.toList())

    override fun render(options: RenderOptions): String {
        val childRenders = children.map { it.render(INTEGER_ARITHMETIC) }.joinToString(" ")
        return "$(($childRenders))"
    }

    override fun replaceChildren(nextChildren: List<BastNode>): IntegerArithmeticBastNode {
        return IntegerArithmeticBastNode(nextChildren.map { it.deepCopy() })
    }
}