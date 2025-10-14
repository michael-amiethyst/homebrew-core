package org.bashpile.core.bast.expressions

import org.bashpile.core.bast.BastNode
import org.bashpile.core.Subshell
import org.bashpile.core.TypeEnum
import org.bashpile.core.engine.RenderOptions

class FloatArithmeticBastNode(children: List<BastNode> = listOf())
    : ArithmeticBastNode(children, majorType = TypeEnum.FLOAT), Subshell {

    constructor(vararg child: BastNode) : this(child.toList())

    override fun render(options: RenderOptions): String {
        val childRenders = children.map { it.render(RenderOptions.UNQUOTED) }.joinToString(" ")
        return "$(bc -l <<< \"$childRenders\")"
    }

    override fun replaceChildren(nextChildren: List<BastNode>): FloatArithmeticBastNode {
        return FloatArithmeticBastNode(nextChildren.map { it.deepCopy() })
    }
}
