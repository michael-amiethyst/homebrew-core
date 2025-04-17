package org.bashpile.core.bast

/** May be true or false, not null */
class BooleanLiteralBastNode(private val bool: Boolean) : BashpileAst(listOf()) {
    override fun render(): String {
        return bool.toString()
    }
}