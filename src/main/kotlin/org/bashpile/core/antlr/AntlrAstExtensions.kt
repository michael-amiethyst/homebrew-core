package org.bashpile.core.antlr

import org.antlr.v4.runtime.tree.TerminalNode
import org.bashpile.core.BashpileLexer
import org.bashpile.core.BashpileParser
import org.bashpile.core.BashpileParser.ExpressionContext


/** Encapsulates Antlr API to preserve the Law of Demeter */
fun BashpileParser.ForeachFileLineLoopStatementContext.statements(): List<BashpileParser.StatementContext> {
    return indentedStatements().statement()
}

/** Encapsulates Antlr API to preserve the Law of Demeter */
fun BashpileParser.PrintStatementContext.expressions(): List<ExpressionContext> {
    return argumentList().expression()
}

/** Encapsulates Antlr API to preserve the Law of Demeter */
fun BashpileParser.VariableDeclarationStatementContext.modifiers(): List<BashpileParser.ModifierContext> {
    return typedId().modifier()
}

/** Encapsulates Antlr API to preserve the Law of Demeter */
fun BashpileParser.VariableDeclarationStatementContext.id(): TerminalNode {
    return typedId().Id()
}

/**
 * Gets the primary (first) type.  E.g., List.
 * Encapsulates Antlr API to preserve the Law of Demeter
 */
fun BashpileParser.VariableDeclarationStatementContext.majorType(): BashpileParser.TypesContext {
    return typedId().majorType()
}

/**
 * Gets the primary (first) type.  E.g., List.
 * Encapsulates Antlr API to preserve the Law of Demeter
 */
fun BashpileParser.TypedIdContext.majorType(): BashpileParser.TypesContext {
    return complexType().types(0)
}

/**
 * Gets the type index of this [TerminalNode].  Indexes are defined in [BashpileLexer].
 * Encapsulates Antlr API to preserve the Law of Demeter
 * @see BashpileLexer.DollarOParen as an example value to match against
 */
fun TerminalNode.typeIndex(): Int {
    return this.symbol.type
}
