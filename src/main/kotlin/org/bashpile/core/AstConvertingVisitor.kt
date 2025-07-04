package org.bashpile.core

import org.antlr.v4.runtime.tree.TerminalNode
import org.bashpile.core.bast.*
import org.bashpile.core.bast.types.BooleanLiteralBastNode
import org.bashpile.core.bast.types.FloatLiteralBastNode
import org.bashpile.core.bast.types.IntLiteralBastNode
import org.bashpile.core.bast.types.LeafBastNode
import org.bashpile.core.bast.types.ReassignmentBastNode
import org.bashpile.core.bast.types.StringLiteralBastNode
import org.bashpile.core.bast.types.TypeEnum
import org.bashpile.core.bast.types.VariableBastNode
import org.bashpile.core.bast.types.VariableDeclarationBastNode

/**
 * Converts Antlr AST (AAST) to Bashpile AST (BAST).
 * Created by [Main].
 * Is in two major sections - Statements and Expressions.
 * Code is arranged from complex at the top to simple at the bottom.
 */
class AstConvertingVisitor: BashpileParserBaseVisitor<BastNode>() {

    override fun visitProgram(ctx: BashpileParser.ProgramContext): BastNode {
        return InternalBastNode(ctx.children.map { visit(it) })
    }

    override fun visitShellLineStatement(ctx: BashpileParser.ShellLineStatementContext): BastNode {
        return ShellLineBastNode(ctx.children.map { visit(it) })
    }

    override fun visitVariableDeclarationStatement(ctx: BashpileParser.VariableDeclarationStatementContext): BastNode {
        val node = visit(ctx.expression())
        val readonly = ctx.modifiers().any { it.text == "readonly" }
        val export = ctx.modifiers().any { it.text == "exported" }
        val id = ctx.id().text
        val typeText = ctx.majorType().text
        val type = TypeEnum.valueOf(typeText.uppercase())
        return VariableDeclarationBastNode(id, type, readonly = readonly, export = export, child = node)
    }

    override fun visitReassignmentStatement(ctx: BashpileParser.ReassignmentStatementContext): BastNode {
        return ReassignmentBastNode(ctx.Id().text, visit(ctx.expression()))
    }

    override fun visitPrintStatement(ctx: BashpileParser.PrintStatementContext): BastNode {
        val nodes = ctx.expressions().map { visit(it) }
        return PrintBastNode(nodes)
    }

    override fun visitExpressionStatement(ctx: BashpileParser.ExpressionStatementContext): BastNode {
        return InternalBastNode(ctx.children.map { visit(it) })
    }

    ///////////////////////////////////
    // expressions
    ///////////////////////////////////

    override fun visitIdExpression(ctx: BashpileParser.IdExpressionContext): BastNode? {
        require(ctx.children.size == 1) { "IdExpression must have exactly one child" }
        return VariableBastNode(ctx.Id().text, TypeEnum.UNKNOWN)
    }

    override fun visitTypedId(ctx: BashpileParser.TypedIdContext): BastNode {
        require(ctx.children.size == 1) { "TypedId must have exactly one child" }
        val primaryTypeString = ctx.majorType().text
        val typeEnum = TypeEnum.valueOf(primaryTypeString.uppercase())
        return VariableBastNode(ctx.Id().text, typeEnum)
    }

    override fun visitParenthesisExpression(ctx: BashpileParser.ParenthesisExpressionContext): BastNode {
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
            val text = stringContext.text
            // remove enclosing double or single quotes
            val trimEnds = stringContext.text.substring(1, text.length - 1)
            StringLiteralBastNode(trimEnds)
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
        val left = visit(ctx.children[0])
        require(left.areAllStrings()) { "Left operand must be all strings, class was ${left.javaClass}" }
        val right = visit(ctx.children[2])
        require(right.areAllStrings()) { "Right operand must be all strings, class was ${right.javaClass}" }
        return InternalBastNode(listOf(left, right))
    }

    // Leaf nodes (parts of expressions)

    override fun visitShellString(ctx: BashpileParser.ShellStringContext): BastNode {
        return ShellStringBastNode(ctx.shellStringContents().map { visit(it )})
    }

    override fun visitShellStringContents(ctx: BashpileParser.ShellStringContentsContext): BastNode {
        // TODO after assignments implemented:
        //  when children are '$(', stuff, and ')'
        //  then "unwind" by moving "stuff" to a preamble node with an assignment
        return InternalBastNode(ctx.children.map { visit(it) })
    }

    override fun visitTerminal(node: TerminalNode): BastNode {
        // antlr may pass us a literal "newline" as the entire node text
        return LeafBastNode(node.text.replace("^newline$".toRegex(), "\n"))
    }
}
