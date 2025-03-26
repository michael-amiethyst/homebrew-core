package org.bashpile.core.bast

class LiteralBastNode(private val text: String) : BashpileAst(listOf()) {
    /** Removes enclosing quotes */
    override fun render(): String {
        return text.removePrefix("\"").removeSuffix("\"")
    }
}