package org.bashpile.core

/** Converts Antlr AST (aast) to Bashpile AST (bast) */
class BashpileVisitor(private val antlrAst: PropertiesParser.ParseContext): PropertiesBaseListener() {
    fun render(): String {
        // TODO convert to bast
        val nextLine = antlrAst.line(0)
        val kw = nextLine.keyValue()
        val key = kw.key().text
        val value = kw.separatorAndValue().chars()
            .joinToString(separator = "") { it.text }

        if (key == "name") {
            return value
        }
        throw RuntimeException("No 'name' found")
    }
}