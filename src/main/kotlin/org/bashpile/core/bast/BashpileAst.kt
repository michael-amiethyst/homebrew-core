package org.bashpile.core.bast

import org.bashpile.core.AstConvertingVisitor

/**
 * Converts internal data to Bashpile.  Created by [AstConvertingVisitor].
 */
// TODO rename to BastNode
open class BashpileAst(protected val children: List<BashpileAst>) {
    open fun render(): String {
        return children.joinToString("") { it.render() }
    }

    /**
     * Are all of the leaves of the AST string literals?
     * Used to ensure that string concatenation is possible.
     */
    fun areAllStringLiterals(): Boolean {
        return if (children.isEmpty()) {
            this is StringLiteralBastNode
        } else {
            children.all { it.areAllStringLiterals() }
        }
    }
}
