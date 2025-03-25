package org.bashpile.core

import org.bashpile.core.bast.BashpileAst
import org.bashpile.core.bast.PrintBastNode

/**
 * Converts Antlr AST (aast) to Bashpile AST (bast).
 * Created by [Main].
 */
class BashpileVisitor: BashpileParserBaseVisitor<BashpileAst>() {

    // TODO visit children
    override fun visitPrintStatement(ctx: BashpileParser.PrintStatementContext): BashpileAst {
        // ctx represents a newline as 'newline' instead of '\n'
        return PrintBastNode(ctx.text.replace("newline$".toRegex(), "\n"))
    }
}
