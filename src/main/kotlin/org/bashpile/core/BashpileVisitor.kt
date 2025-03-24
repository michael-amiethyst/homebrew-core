package org.bashpile.core

import org.bashpile.core.bast.PrintBastNode

/** Converts Antlr AST (aast) to Bashpile AST (bast) */
class BashpileVisitor: BashpileParserBaseVisitor<BashpileAst>() {
    // TODO visit children
    override fun visitProgram(antlrAst: BashpileParser.ProgramContext): BashpileAst {
        val firstStatementText: String =
            if (antlrAst.statement().isNotEmpty()) antlrAst.statement(0).text else "Hello World"
        return PrintBastNode(firstStatementText)
    }
}
