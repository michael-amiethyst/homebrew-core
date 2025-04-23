package org.bashpile.core.bast

import org.bashpile.core.AstConvertingVisitor

/**
 * Converts internal data to Bashpile.  Created by [AstConvertingVisitor].
 */
open class BashpileAst(protected val children: List<BashpileAst>) {
    open fun render(): String {
        return children.joinToString("") { it.render() }
    }

    fun areAllStringLiterals(): Boolean {
        return if (children.isEmpty()) {
            this is StringLiteralBastNode
        } else {
            children.all { it.areAllStringLiterals() }
        }
    }
}
