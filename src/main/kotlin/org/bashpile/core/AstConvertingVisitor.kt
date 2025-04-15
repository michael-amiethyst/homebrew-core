package org.bashpile.core

import org.bashpile.core.BashpileParser.ExpressionContext
import org.bashpile.core.bast.BashpileAst
import org.bashpile.core.bast.BooleanLiteralBastNode
import org.bashpile.core.bast.StringLiteralBastNode
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
        if (ctx.BoolValues() != null) {
            return BooleanLiteralBastNode(ctx.BoolValues().text.toBoolean())
        } else if (ctx.NumberValues() != null) {
            // TODO: Implement number literals
            throw IllegalArgumentException("Number values are not supported yet")
        } else if (ctx.StringValues() != null) {
            return StringLiteralBastNode(ctx.StringValues().text)
        } else {
            throw IllegalArgumentException("Unknown literal type")
        }
    }

    /** Encapsulates Antlr context API */
    private fun BashpileParser.PrintStatementContext.expressions(): List<ExpressionContext> {
        return argumentList().expression() // known Law of Demeter violation
    }

    override fun visitCalculationExpression(ctx: BashpileParser.CalculationExpressionContext): BashpileAst {
        require(ctx.children.size == 3) { "Calculation expression must have 3 children" }
        require(ctx.children[1].text == "+") { "Only addition is supported" }
        require(visit(ctx.children[0]) is StringLiteralBastNode) { "Left operand must be a string" }
        require(visit(ctx.children[2]) is StringLiteralBastNode) { "Right operand must be a string" }
        return BashpileAst(listOf(visit(ctx.children[0]), visit(ctx.children[2])))
    }
}
