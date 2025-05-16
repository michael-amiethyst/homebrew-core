package org.bashpile.core.bast

import org.apache.commons.lang3.StringUtils

class ShellLineBastNode(private val text: String) : BastNode(listOf()) {
    override fun render(): String {
        return text.appendIfMissing("\n")
    }

    private fun String.appendIfMissing(suffix: String): String {
        return StringUtils.appendIfMissing(this, suffix)
    }
}
