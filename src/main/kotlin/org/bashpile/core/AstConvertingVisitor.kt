package org.bashpile.core

import org.antlr.v4.runtime.tree.TerminalNode
import org.bashpile.core.BashpileParser.ExpressionContext
import org.bashpile.core.bast.*

/**
 * Converts Antlr AST (aast) to Bashpile AST (bast).
 * Created by [Main].
 */
class AstConvertingVisitor: BashpileParserBaseVisitor<BastNode>() {

    override fun visitPrintStatement(ctx: BashpileParser.PrintStatementContext): BastNode {
        val nodes = ctx.expressions().map { visit(it) }
        return PrintBastNode(nodes)
    }

    override fun visitShellLineStatement(ctx: BashpileParser.ShellLineStatementContext): BastNode? {
        // TODO get rid of .render, have ShellLineBastNode track children instead of a string
        return ShellLineBastNode(ctx.children.map { visit(it).render() }.joinToString(""))
    }

    override fun visitExpressionStatement(ctx: BashpileParser.ExpressionStatementContext): BastNode? {
        return BastNode(ctx.children.map { visit(it) })
    }

    /** Encapsulates Antlr API to preserve Law of Demeter */
    private fun BashpileParser.PrintStatementContext.expressions(): List<ExpressionContext> {
        return argumentList().expression()
    }

    ///////////////////////////////////
    // expressions
    ///////////////////////////////////

    override fun visitParenthesisExpression(ctx: BashpileParser.ParenthesisExpressionContext): BastNode? {
        // strip parenthesis until calc implemented
        check(ctx.childCount == 3)
        return visit(ctx.children[1])
    }

    override fun visitLiteral(ctx: BashpileParser.LiteralContext): BastNode {
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

    override fun visitNumberExpression(ctx: BashpileParser.NumberExpressionContext): BastNode {
        val nodeText = ctx.text
        return if (nodeText.contains('.')) {
            FloatLiteralBastNode(nodeText.toBigDecimal())
        } else {
            IntLiteralBastNode(nodeText.toBigInteger())
        }
    }

    override fun visitCalculationExpression(ctx: BashpileParser.CalculationExpressionContext): BastNode {
        require(ctx.children.size == 3) { "Calculation expression must have 3 children" }
        require(ctx.children[1].text == "+") { "Only addition is supported" }
        require(visit(ctx.children[0]).areAllStringLiterals()) { "Left operand must be all strings" }
        require(visit(ctx.children[2]).areAllStringLiterals()) { "Right operand must be all strings" }
        return BastNode(listOf(visit(ctx.children[0]), visit(ctx.children[2])))
    }

    // Leaf nodes (parts of expressions)

    override fun visitShellString(ctx: BashpileParser.ShellStringContext): BastNode? {
        check(ctx.children.size == 3) { "ShellStringContext must have exactly 3 children" }
        return ShellStringBastNode(ctx.shellStringContents().map { visit(it )})
    }

    override fun visitTerminal(node: TerminalNode): BastNode? {
        // antlr may pass us a literal "newline" as the entire node text
        return LeafBastNode(node.text.replace("^newline$".toRegex(), "\n"))
    }
}
