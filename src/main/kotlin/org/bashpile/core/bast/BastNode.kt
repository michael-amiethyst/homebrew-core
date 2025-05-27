package org.bashpile.core.bast

import org.bashpile.core.AstConvertingVisitor
import org.bashpile.core.Main
import org.bashpile.core.bast.types.LeafBastNode
import org.bashpile.core.bast.types.TypeEnum
import org.bashpile.core.bast.types.VariableTypeInfo


/**
 * The base class of the BAST class hierarchy.
 * Converts this AST and children to the Bashpile text output via [render].
 * The root is created by the [AstConvertingVisitor].
 */
abstract class BastNode(
    protected val children: List<BastNode>, val id: String? = null, val type: TypeEnum = TypeEnum.UNKNOWN) {

    fun resolvedType(): TypeEnum {
        // TODO assignment - factor Main.instance into a BashpileState object
        return Main.instance.stackframe.find { it.id == id }?.type ?: type
    }

    fun variableInfo(): VariableTypeInfo? {
        return Main.instance.stackframe.find { it.id == id }
    }

    open fun render(): String {
        return children.joinToString("") { it.render() }
    }

    /**
     * Are all the leaves of the AST string literals?
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
