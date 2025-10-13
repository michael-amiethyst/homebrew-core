package org.bashpile.core.bast.expressions

import org.bashpile.core.TypeEnum.STRING
import org.bashpile.core.bast.BastNode

class StringConcatenationBastNode(childern: List<BastNode>)
    : BastNode(childern.toMutableList(), majorType = STRING)
{
    override fun render(): String {
        val childRenders = children.map { it.render() }.map {
            it.removeSurrounding("\"").removeSurrounding("'")
        }.joinToString("")
        return if (parents().any { it is UnaryPrimaryBastNode || it is BinaryPrimaryBastNode } ) {
            """
                "$childRenders"
            """.trimIndent()
        } else { childRenders }
    }

    override fun replaceChildren(nextChildren: List<BastNode>): StringConcatenationBastNode {
        return StringConcatenationBastNode(nextChildren.map { it.deepCopy() })
    }
}
