package org.bashpile.core.antlr

import org.antlr.v4.runtime.tree.TerminalNode
import org.bashpile.core.BashpileParser
import org.bashpile.core.BashpileParserBaseVisitor
import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.InternalBastNode
import org.bashpile.core.bast.expressions.FloatArithmeticBastNode
import org.bashpile.core.bast.expressions.IntegerArithmeticBastNode
import org.bashpile.core.bast.expressions.LooseShellStringBastNode
import org.bashpile.core.bast.expressions.ParenthesisBastNode
import org.bashpile.core.bast.expressions.ShellStringBastNode
import org.bashpile.core.bast.statements.PrintBastNode
import org.bashpile.core.bast.statements.ReassignmentBastNode
import org.bashpile.core.bast.statements.ShellLineBastNode
import org.bashpile.core.bast.statements.VariableDeclarationBastNode
import org.bashpile.core.bast.types.BooleanLiteralBastNode
import org.bashpile.core.bast.types.FloatLiteralBastNode
import org.bashpile.core.bast.types.IntegerLiteralBastNode
import org.bashpile.core.bast.types.StringLiteralBastNode
import org.bashpile.core.bast.types.TypeEnum
import org.bashpile.core.bast.types.VariableBastNode
import org.bashpile.core.bast.types.leaf.ClosingParenthesisLeafBastNode
import org.bashpile.core.bast.types.leaf.LeafBastNode
import org.bashpile.core.bast.types.leaf.SubshellStartLeafBastNode

/**
 * Converts Antlr AST (AAST) to Bashpile AST (BAST).
 * Created by [org.bashpile.core.Main].
 * Is in two major sections - Statements and Expressions.
 * Code is arranged from complex at the top to simple at the bottom.
 */
class AstConvertingVisitor: BashpileParserBaseVisitor<BastNode>() {

    companion object {
        const val OLD_OPTIONS = "__bp_old_options"

        /** See [Unofficial Bash Strict Mode](http://redsymbol.net/articles/unofficial-bash-strict-mode/) */
        const val ENABLE_STRICT = "set -euo pipefail"

        @JvmStatic
        val STRICT_HEADER = """
            declare -i s
            trap 's=$?; echo "Error (exit code ${'$'}s) found on line ${'$'}LINENO of generated Bash.\
              Command was: ${'$'}BASH_COMMAND"; exit ${'$'}s' ERR
            declare $OLD_OPTIONS
            $OLD_OPTIONS=$(set +o)
            $ENABLE_STRICT

        """.trimIndent()
    }

    override fun visitProgram(ctx: BashpileParser.ProgramContext): BastNode {
        val statementNodes = ShellLineBastNode(STRICT_HEADER).toList() + ctx.children.map { visit(it) }
        return InternalBastNode(statementNodes)
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
        check(ctx.childCount == 3)
        val child = visit(ctx.children[1])
        return ParenthesisBastNode(child.toList(), child.majorType)
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
            IntegerLiteralBastNode(nodeText.toBigInteger())
        }
    }

    override fun visitCalculationExpression(ctx: BashpileParser.CalculationExpressionContext): BastNode {
        require(ctx.children.size == 3) { "Calculation expression must have 3 children" }
        val left: BastNode = visit(ctx.children[0])
        val middle = visit(ctx.children[1])
        val right = visit(ctx.children[2])
        return if (left.areAllStrings() && right.areAllStrings()) {
            require(ctx.children[1].text == "+") { "Only addition is supported on strings" }
            InternalBastNode(left, right)
        } else if (left.majorType == TypeEnum.INTEGER && right.majorType == TypeEnum.INTEGER) {
            IntegerArithmeticBastNode(left, middle, right)
        } else if (left.majorType.coercesTo(TypeEnum.FLOAT) && right.majorType.coercesTo(TypeEnum.FLOAT)) {
            FloatArithmeticBastNode(left, middle, right)
        } else {
            throw UnsupportedOperationException(
                "Only calculations on all Strings or all numbers are supported, " +
                        "but found ${left.majorType} and ${right.majorType}")
        }
    }

    override fun visitTypecastExpression(ctx: BashpileParser.TypecastExpressionContext): BastNode {
        val aastChildren = ctx.children
        require(aastChildren.size == 3)
        val typecastTo = aastChildren[2].text
        val nextType = TypeEnum.valueOf(typecastTo.uppercase())
        val bastExpression = visit(aastChildren[0])
        return InternalBastNode(bastExpression.toList(), nextType)
    }

    // Leaf nodes (parts of expressions)

    override fun visitShellString(ctx: BashpileParser.ShellStringContext): BastNode {
        return ShellStringBastNode(ctx.shellStringContents().map { visit(it) })
    }

    override fun visitLooseShellString(ctx: BashpileParser.LooseShellStringContext): BastNode {
        return LooseShellStringBastNode(ctx.shellStringContents().map { visit(it) })
    }

    override fun visitShellStringContents(ctx: BashpileParser.ShellStringContentsContext): BastNode {
        val bastChildren = ctx.children.map { visit(it) }
        val isNestedSubshell = bastChildren.size == 3
                && bastChildren[0] is SubshellStartLeafBastNode
                && bastChildren[2] is ClosingParenthesisLeafBastNode
        return if (bastChildren.size == 1) {
            bastChildren[0]
        } else if (isNestedSubshell) {
            // middle child only
            ShellStringBastNode(bastChildren.subList(1, 2))
        } else {
            InternalBastNode(bastChildren)
        }
    }

    override fun visitTerminal(node: TerminalNode): BastNode {
        return when (node.text) {
            SubshellStartLeafBastNode.Companion.SUBSHELL_START -> SubshellStartLeafBastNode()
            ClosingParenthesisLeafBastNode.Companion.CLOSING_PARENTHESIS -> ClosingParenthesisLeafBastNode()
            else -> return LeafBastNode(node.text.replace("^newline$".toRegex(), "\n"))
        }
    }
}
