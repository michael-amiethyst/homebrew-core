package org.bashpile.core.bast

class LeafBastNode(private val text: String) : BastNode(listOf()) {
    override fun render(): String {
        return text
    }
}
