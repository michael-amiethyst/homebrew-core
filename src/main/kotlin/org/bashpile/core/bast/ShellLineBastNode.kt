package org.bashpile.core.bast

import org.apache.commons.lang3.StringUtils

class ShellLineBastNode(children: List<BastNode>) : BastNode(children) {
    override fun render(): String {
        val childRenders = children.map { it.render() }.joinToString("")
        return childRenders.appendIfMissing("\n")
    }

    private fun String.appendIfMissing(suffix: String): String {
        return StringUtils.appendIfMissing(this, suffix)
    }
}
