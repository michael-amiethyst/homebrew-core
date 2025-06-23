package org.bashpile.core.bast

import org.bashpile.core.bast.types.ClosingParenthesisLeafBastNode
import org.bashpile.core.bast.types.LeafBastNode
import org.bashpile.core.bast.types.SubshellStartLeafBastNode
import org.bashpile.core.bast.types.TypeEnum.UNKNOWN
import org.bashpile.core.bast.types.VariableBastNode
import org.bashpile.core.bast.types.VariableDeclarationBastNode


/**
 * For inner nodes in the tree data structure sense.  Used as a "holder node".
 *
 * @see https://en.wikipedia.org/wiki/Tree_(abstract_data_type)#:~:text=An%20internal%20node
 */
class InternalBastNode(children: List<BastNode> = listOf()) : BastNode(children) {
    override fun deepCopy(): InternalBastNode {
        return InternalBastNode(children.map { it.deepCopy() })
    }

    // TODO move to BastNode
    /** @return Self unchanged or InternalNode holding an assignment and variable reference */
    fun unnestSubshells(): BastNode {
        if (nestedSubshell()) {
            // TODO unnest - finish, account for double nesting
            // get subshell text
            val nestedSubshellText = ""
            val subshellNode = ShellStringBastNode(listOf(LeafBastNode(nestedSubshellText)))

            // create assignment statement
            val id = "__bp_var0"
            val assignment = VariableDeclarationBastNode(id, UNKNOWN, child = subshellNode)

            // create VarDec node
            val variableReference = VariableBastNode(id, UNKNOWN)

            // TODO unnest -- implement parentSubshell
            val preamble =  InternalBastNode(listOf(assignment, variableReference))
            val parentSubshell = InternalBastNode(listOf(LeafBastNode("before nested"), variableReference, LeafBastNode("after nested")))
            return InternalBastNode(listOf(preamble, parentSubshell))
        }
        // TODO unnest - make recursive call
        return this.deepCopy()
    }

    fun nestedSubshell(): Boolean {
        return children.size == 3
                && children.first() is SubshellStartLeafBastNode
                && children.last() is ClosingParenthesisLeafBastNode
    }
}
