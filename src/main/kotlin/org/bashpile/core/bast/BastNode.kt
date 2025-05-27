package org.bashpile.core.bast

import org.bashpile.core.AstConvertingVisitor
import org.bashpile.core.bast.types.LeafBastNode
import org.bashpile.core.bast.types.TypeEnum


/**
 * The base class of the BAST class hierarchy.
 * Converts this AST and children to the Bashpile text output via [render].
 * The root is created by the [AstConvertingVisitor].
 */
abstract class BastNode(protected val children: List<BastNode>, val type: TypeEnum = TypeEnum.UNKNOWN) {

    // TODO assignments - add init to put id and type on the stack if both set
    // TODO assignments - add type getter to check the stack, override if type is unknown and check for a match otherwise
    open fun render(): String {
        return children.joinToString("") { it.render() }
    }

    /**
     * Are all of the leaves of the AST string literals?
     * Used to ensure that string concatenation is possible.
     */
    fun areAllStrings(): Boolean {
        return if (children.isEmpty()) {
            this is StringLiteralBastNode || this is ShellStringBastNode || this is LeafBastNode
        } else {
            children.all { it.areAllStrings() }
        }
    }
}
