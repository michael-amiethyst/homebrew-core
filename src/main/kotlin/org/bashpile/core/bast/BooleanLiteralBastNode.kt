package org.bashpile.core.bast

class BooleanLiteralBastNode(private val bool: Boolean) : BashpileAst(listOf()) {
    override fun render(): String {
        return bool.toString()
    }
}