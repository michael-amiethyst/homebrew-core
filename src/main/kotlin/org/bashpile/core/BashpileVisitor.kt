package org.bashpile.core

/** Converts Antlr AST (aast) to Bashpile AST (bast) */
class BashpileVisitor: PropertiesBaseVisitor<BashpileAst>() {
    override fun visitParse(antlrAst: PropertiesParser.ParseContext): BashpileAst {
        return BashpileAst(antlrAst)
    }
}
