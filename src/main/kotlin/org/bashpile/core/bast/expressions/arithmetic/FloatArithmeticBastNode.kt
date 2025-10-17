package org.bashpile.core.bast.expressions.arithmetic

import org.bashpile.core.Subshell
import org.bashpile.core.TypeEnum.FLOAT
import org.bashpile.core.bast.BastNode
import org.bashpile.core.engine.RenderOptions
import org.bashpile.core.engine.RenderOptions.Companion.UNQUOTED

class FloatArithmeticBastNode(children: List<BastNode> = listOf())
    : ArithmeticBastNode(children, majorType = FLOAT), Subshell
{
    constructor(vararg child: BastNode) : this(child.toList())

    override fun render(options: RenderOptions): String {
        // output of `bc` may have spaces
        val childRenders = children.map { it.render(UNQUOTED) }.joinToString(" ")
        val bcSubshell = "$(bc -l <<< \"$childRenders\")"
        return if (options.quoted) { "\"$bcSubshell\"" } else { bcSubshell }
    }

    override fun replaceChildren(nextChildren: List<BastNode>): FloatArithmeticBastNode {
        return FloatArithmeticBastNode(nextChildren.map { it.deepCopy() })
    }
}