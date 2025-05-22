package org.bashpile.core.bast

class StringLiteralBastNode(private val text: String) : BastNode(listOf()) {
    /** Removes enclosing quotes */
    override fun render(): String {
        return text.removePrefix("\"").removeSuffix("\"")
    }
}
