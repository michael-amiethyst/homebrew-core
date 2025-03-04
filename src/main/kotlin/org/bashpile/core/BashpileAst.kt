package org.bashpile.core

/** Created by [BashpileVisitor] */
class BashpileAst(private val antlrAst: PropertiesParser.ParseContext) {
    // TODO move antlr logic into BashpileVisitor
    fun render(): String? {
        val nextLine = antlrAst.line(0)
        val kw = nextLine.keyValue()
        val key = kw.key().text
        val value = kw.separatorAndValue().chars()
            .joinToString(separator = "") { it.text }
        return if (key == "name") value else null
    }
}