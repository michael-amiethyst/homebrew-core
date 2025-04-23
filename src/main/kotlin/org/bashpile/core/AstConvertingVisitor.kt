package org.bashpile.core

import org.bashpile.core.BashpileParser.ExpressionContext
import org.bashpile.core.bast.*

/**
 * Converts Antlr AST (aast) to Bashpile AST (bast).
 * Created by [Main].
 */
class AstConvertingVisitor: BashpileParserBaseVisitor<BashpileAst>() {

    override fun visitPrintStatement(ctx: BashpileParser.PrintStatementContext): BashpileAst {
        val nodes = ctx.expressions().map { visit(it) }
        return PrintBastNode(nodes)
    }

    /** Encapsulates Antlr context API */
    private fun BashpileParser.PrintStatementContext.expressions(): List<ExpressionContext> {
        return argumentList().expression() // known Law of Demeter violation
    }

    override fun visitLiteral(ctx: BashpileParser.LiteralContext): BashpileAst {
        val boolContext = ctx.BoolValues()
        val stringContext = ctx.StringValues()

        return if (boolContext != null) {
            BooleanLiteralBastNode(boolContext.text.toBoolean())
        } else if (stringContext != null) {
            StringLiteralBastNode(stringContext.text)
        } else {
            val message = "Unknown literal type.  Numeric values should be handled in visitNumberExpression"
            throw IllegalArgumentException(message)
        }
    }

    override fun visitNumberExpression(ctx: BashpileParser.NumberExpressionContext): BashpileAst {
        val nodeText = ctx.text
        return if (nodeText.contains('.')) {
            FloatLiteralBastNode(nodeText.toBigDecimal())
        } else {
            IntLiteralBastNode(nodeText.toBigInteger())
        }
    }

    override fun visitCalculationExpression(ctx: BashpileParser.CalculationExpressionContext): BashpileAst {
        require(ctx.children.size == 3) { "Calculation expression must have 3 children" }
        require(ctx.children[1].text == "+") { "Only addition is supported" }
        // TODO write BashpileAst.areAllStringLiterals recursive function, write test, use here
        require(visit(ctx.children[0]).areAllStringLiterals()) { "Left operand must be all strings" }
        require(visit(ctx.children[2]).areAllStringLiterals()) { "Right operand must be all strings" }
        return BashpileAst(listOf(visit(ctx.children[0]), visit(ctx.children[2])))
    }
}
