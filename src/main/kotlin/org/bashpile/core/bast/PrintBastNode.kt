package org.bashpile.core.bast

class PrintBastNode(private val firstStatementText: String) : BashpileAst(null) {

    // TODO have child text node
    override fun render(): String {
        return firstStatementText
            .removePrefix("print(\"")
            // remove trailing ")
            .replace("\"\\)\n$".toRegex(), "\n")
    }
}