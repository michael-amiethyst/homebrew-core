package org.bashpile.core.bast

import org.apache.commons.lang3.StringUtils


class ShellLineBastNode(children: List<BastNode> = listOf()) : BastNode(children) {
    override fun render(): RenderTuple {
        val childRenders = children.map { it.render().second }.joinToString("")
        return Pair(listOf(), childRenders.appendIfMissing("\n"))
    }

    override fun replaceChildren(nextChildren: List<BastNode>): BastNode {
        return ShellLineBastNode(nextChildren.map { it.deepCopy() })
    }

    private fun String.appendIfMissing(suffix: String): String {
        return StringUtils.appendIfMissing(this, suffix)
    }
}
