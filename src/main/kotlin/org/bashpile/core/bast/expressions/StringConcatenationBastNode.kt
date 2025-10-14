package org.bashpile.core.bast.expressions

import org.bashpile.core.TypeEnum.STRING
import org.bashpile.core.bast.BastNode

class StringConcatenationBastNode(children: List<BastNode>)
    : BastNode(children.toMutableList(), majorType = STRING)
{
    override fun render(): String {
        val childRenders = children.map { it.render() }.map {
            it.removeSurrounding("\"").removeSurrounding("'")
        }.joinToString("")
        val quoteNeeded = parent!! !is StringConcatenationBastNode && parents().any { it is PrimaryBastNode }
        return if (quoteNeeded) {
            """
                "$childRenders"
            """.trimIndent()
        } else { childRenders }
    }

    override fun replaceChildren(nextChildren: List<BastNode>): StringConcatenationBastNode {
        return StringConcatenationBastNode(nextChildren.map { it.deepCopy() })
    }
}
