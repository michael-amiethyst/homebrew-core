package org.bashpile.core

/** Created by [BashpileVisitor] */
class BashpileAst(private val antlrAst: BashpileParser.ProgramContext) {
    // TODO move antlr logic into BashpileVisitor
    fun render(): String? {
        val firstStatementText = if (antlrAst.statement().isNotEmpty()) antlrAst.statement(0).text else null
        return firstStatementText?.replace("newline", "")
    }
}
