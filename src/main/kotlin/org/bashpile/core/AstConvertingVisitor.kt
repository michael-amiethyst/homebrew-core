package org.bashpile.core

import org.bashpile.core.BashpileParser.ExpressionContext
import org.bashpile.core.bast.BashpileAst
import org.bashpile.core.bast.LiteralBastNode
import org.bashpile.core.bast.PrintBastNode

/**
 * Converts Antlr AST (aast) to Bashpile AST (bast).
 * Created by [Main].
 */
class AstConvertingVisitor: BashpileParserBaseVisitor<BashpileAst>() {

    override fun visitPrintStatement(ctx: BashpileParser.PrintStatementContext): BashpileAst {
        val nodes = ctx.expressions().map { visit(it) }
        return PrintBastNode(nodes)
    }
    
    override fun visitLiteral(ctx: BashpileParser.LiteralContext): BashpileAst {
        return LiteralBastNode(ctx.text)
    }

    private fun BashpileParser.PrintStatementContext.expressions(): List<ExpressionContext> {
        return argumentList().expression()
    }
}
