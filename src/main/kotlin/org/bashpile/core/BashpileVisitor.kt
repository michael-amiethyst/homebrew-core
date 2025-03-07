package org.bashpile.core

/** Converts Antlr AST (aast) to Bashpile AST (bast) */
class BashpileVisitor: BashpileParserBaseVisitor<BashpileAst>() {
    override fun visitProgram(antlrAst: BashpileParser.ProgramContext): BashpileAst {
        return BashpileAst(antlrAst)
    }
}
