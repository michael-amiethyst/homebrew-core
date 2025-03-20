package org.bashpile.core

/** Created by [BashpileVisitor] */
class BashpileAst(private val firstStatementText: String) {
    // TODO add enum for type
    fun render(): String {
        return firstStatementText.replace("newline", "")
    }
}
