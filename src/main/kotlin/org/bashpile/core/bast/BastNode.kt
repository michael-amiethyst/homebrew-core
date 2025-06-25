package org.bashpile.core.bast

import org.bashpile.core.AstConvertingVisitor
import org.bashpile.core.Main.Companion.bashpileState
import org.bashpile.core.bast.types.ClosingParenthesisLeafBastNode
import org.bashpile.core.bast.types.LeafBastNode
import org.bashpile.core.bast.types.StringLiteralBastNode
import org.bashpile.core.bast.types.SubshellStartLeafBastNode
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
    val majorType: TypeEnum = UNKNOWN) {

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

    private fun unnestSubshells(inSubshell: Boolean): BastNode {
        if (inSubshell && isASubshell()) {
            // TODO unnest - finish, account for double nesting
            // get subshell text
            val nestedSubshellText = ""
            val subshellNode = ShellStringBastNode(listOf(LeafBastNode(nestedSubshellText)))

            // create assignment statement
            val id = "__bp_var0" // TODO unnest - generate var names
            val assignment = VariableDeclarationBastNode(id, UNKNOWN, child = subshellNode)

            // create VarDec node
            val variableReference = VariableBastNode(id, UNKNOWN)

            // TODO unnest -- implement parentSubshell
            val preamble =  InternalBastNode(listOf(assignment, variableReference))
            val parentSubshell = InternalBastNode(listOf(LeafBastNode("before nested"), variableReference, LeafBastNode("after nested")))
            return InternalBastNode(listOf(preamble, parentSubshell))
        }
        return replaceChildren(children.map { it.unnestSubshells(isASubshell()) })
    }

    private fun isASubshell(): Boolean {
        return children.size == 3
                && children.first() is SubshellStartLeafBastNode
                && children.last() is ClosingParenthesisLeafBastNode
    }
}
