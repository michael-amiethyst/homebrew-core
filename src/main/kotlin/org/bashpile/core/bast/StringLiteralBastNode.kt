package org.bashpile.core.bast

// TODO assign refactor out
class StringLiteralBastNode(private val text: String) : BastNode(listOf()) {
    override fun render(): String {
        return text
    }
}
