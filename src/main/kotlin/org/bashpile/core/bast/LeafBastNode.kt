package org.bashpile.core.bast

class LeafBastNode(private val text: String) : BashpileAst(listOf()) {
    override fun render(): String {
        return text
    }
}
