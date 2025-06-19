package org.bashpile.core.bast

import org.apache.commons.lang3.StringUtils


class ShellLineBastNode(children: List<BastNode>) : BastNode(children) {
    override fun render(): String {
        val childRenders = children.map { it.render() }.joinToString("")
        return childRenders.appendIfMissing("\n")
    }

    override fun deepCopy(): ShellLineBastNode {
        return ShellLineBastNode(children.map { it.deepCopy() })
    }

    private fun String.appendIfMissing(suffix: String): String {
        return StringUtils.appendIfMissing(this, suffix)
    }
}
