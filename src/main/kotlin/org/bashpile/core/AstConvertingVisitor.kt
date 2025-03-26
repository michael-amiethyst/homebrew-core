package org.bashpile.core

import org.bashpile.core.bast.BashpileAst
import org.bashpile.core.bast.LiteralBastNode
import org.bashpile.core.bast.PrintBastNode

/**
 * Converts Antlr AST (aast) to Bashpile AST (bast).
 * Created by [Main].
 */
class AstConvertingVisitor: BashpileParserBaseVisitor<BashpileAst>() {

    // TODO visit children
    override fun visitPrintStatement(ctx: BashpileParser.PrintStatementContext): BashpileAst {
        // ctx represents a newline as 'newline' instead of '\n'
        val argumentText = ctx.argumentList().expression(0).text
        val node = LiteralBastNode(argumentText)
        return PrintBastNode(node)
    }
}
