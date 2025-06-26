package org.bashpile.core.bast

import com.google.common.annotations.VisibleForTesting
import org.bashpile.core.AstConvertingVisitor
import org.bashpile.core.Main.Companion.bashpileState
import org.bashpile.core.bast.types.LeafBastNode
import org.bashpile.core.bast.types.StringLiteralBastNode
import org.bashpile.core.bast.types.TypeEnum
import org.bashpile.core.bast.types.TypeEnum.UNKNOWN
import org.bashpile.core.bast.types.VariableBastNode
import org.bashpile.core.bast.types.VariableDeclarationBastNode
import org.bashpile.core.bast.types.VariableTypeInfo


/**
 * The base class of the BAST class hierarchy.
 * Converts this AST and children to the Bashpile text output via [render].
 * The root is created by the [AstConvertingVisitor].
 * Sometimes the type of a node isn't known at creation time, so the type is on the call stack at [BashpileState].
 */
abstract class BastNode(
    val children: List<BastNode>,
    val id: String? = null,
    /** The type at creation time see class KDoc for more info */
    val majorType: TypeEnum = UNKNOWN
) {
    companion object {
        @VisibleForTesting
        var unnestedCount: Int = 0
    }

    fun resolvedMajorType(): TypeEnum {
        // check call stack, fall back on node's type
        return bashpileState.variableInfo(id)?.majorType ?: majorType
    }

    fun variableInfo(): VariableTypeInfo? {
        return bashpileState.variableInfo(id)
    }

    /** Should be just string manipulation to make final Bashpile text, no logic */
    open fun render(): Pair<List<BastNode>, String> {
        val renders = children.map { it.render()}
        return Pair(renders.flatMap { it.first }, renders.map { it.second }.joinToString("") )
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

    fun deepCopy(): BastNode {
        return replaceChildren(this.children)
    }

    /**
     * @param replaceChildren will not be modified
     * @return A new instance of a BastNode subclass with the same fields, besides the children
     */
    abstract fun replaceChildren(nextChildren: List<BastNode>): BastNode

    /** @return Self unchanged or InternalNode holding an assignment and variable reference */
    fun unnestSubshells(): BastNode {
        return unnestSubshells(false)
    }

    // TODO create statement parent class, render preambles there?
    // TODO ensure all statement nodes render preambles
    private fun unnestSubshells(inSubshell: Boolean): BastNode {
        if (inSubshell && this is ShellStringBastNode) {
            // create an assignment statement
            val id = "__bp_var${unnestedCount++}"
            val assignment = VariableDeclarationBastNode(id, UNKNOWN, child = deepCopy())

            // create VarDec node
            val variableReference = VariableBastNode(id, UNKNOWN)
            // statement nodes render the preambles, and return empty list of preambles to parent
            return UnnestedShellStringBastNode(listOf(assignment, variableReference))
        }
        return replaceChildren(children.map { it.unnestSubshells(this is ShellStringBastNode) })
    }
}
