package org.bashpile.core.bast

import org.bashpile.core.AstConvertingVisitor

/**
 * The base class of the BAST class hierarchy, and it may be used as a root node as is.
 * Converts this AST and children to the Bashpile text output via [render].
 * The root is created by the [AstConvertingVisitor].  Subclasses also create manually as an internal node.
 */
open class BastNode(protected val children: List<BastNode>) {
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
