package org.bashpile.core.bast

import org.bashpile.core.AstConvertingVisitor
import org.bashpile.core.Main.Companion.bashpileState
import org.bashpile.core.bast.types.LeafBastNode
import org.bashpile.core.bast.types.StringLiteralBastNode
import org.bashpile.core.bast.types.TypeEnum
import org.bashpile.core.bast.types.VariableTypeInfo


/**
 * The base class of the BAST class hierarchy.
 * Converts this AST and children to the Bashpile text output via [render].
 * The root is created by the [AstConvertingVisitor].
 * Sometimes the type of a node isn't known at creation time, so the type is on the call stack at [BashpileState].
 */
abstract class BastNode(
    protected val children: List<BastNode>,
    val id: String? = null,
    /** The type at creation time see class KDoc for more info */
    val majorType: TypeEnum = TypeEnum.UNKNOWN) {

    fun resolvedMajorType(): TypeEnum {
        // check call stack, fall back on node's type
        return bashpileState.variableInfo(id)?.majorType ?: majorType
    }

    fun variableInfo(): VariableTypeInfo? {
        return bashpileState.variableInfo(id)
    }

    /** Should be just string manipulation to make final Bashpile text, no logic */
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
