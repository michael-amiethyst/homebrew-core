package org.bashpile.core.bast

import org.bashpile.core.BashpileAst

class PrintBastNode(private val firstStatementText: String) : BashpileAst(null) {

    // TODO have child text node
    override fun render(): String {
        return firstStatementText.replace("newline", "")
            .removePrefix("print(\"")
            .removeSuffix("\")")
    }
}