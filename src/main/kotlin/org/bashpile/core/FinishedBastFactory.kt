package org.bashpile.core

import org.apache.logging.log4j.LogManager
import org.bashpile.core.FinishedBastFactory.Companion.unnestedCount
import org.bashpile.core.FinishedBastFactory.Companion.unnestedCountLock
import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.InternalBastNode
import org.bashpile.core.bast.UnnestTuple
import org.bashpile.core.bast.expressions.LooseShellStringBastNode
import org.bashpile.core.bast.expressions.ShellStringBastNode
import org.bashpile.core.bast.statements.StatementBastNode
import org.bashpile.core.bast.statements.VariableDeclarationBastNode
import org.bashpile.core.bast.types.TypeEnum.UNKNOWN
import org.bashpile.core.bast.types.VariableBastNode


/** @return An unnested version of the input tree */
fun BastNode.unnestSubshells(): BastNode {
    synchronized(unnestedCountLock) {
        unnestedCount = 0
        val hasLooseShellStringChild = findInTree { it is LooseShellStringBastNode }
        if (hasLooseShellStringChild) {
            return this
        }
        val unnestedRoot = unnestSubshells(isSubshellNode())
        return if (unnestedRoot.first.isEmpty()) {
            // no unnesting performed
            unnestedRoot.second
        } else {
            InternalBastNode(unnestedRoot.first + unnestedRoot.second)
        }
    }
}

/**
 * Returns a list of preambles to support unnesting.
 * @return Preambles and unnested subshell.
 * @see /documentation/contributing/unnest.md
 */
private fun BastNode.unnestSubshells(inSubshell: Boolean): UnnestTuple {
    val unnestedChildPairs = children.map { it.unnestSubshells(inSubshell || isSubshellNode()) }
    val unnestedPreambles = unnestedChildPairs.flatMap { it.first }
    val unnestedChildren = unnestedChildPairs.map { it.second }

    val currentNodeIsNested = inSubshell && isSubshellNode()
    val noNestedChildren = unnestedPreambles.isEmpty()
    return if (!currentNodeIsNested && noNestedChildren) {
        Pair(listOf(), replaceChildren(unnestedChildren))
    } else if (currentNodeIsNested) {
        // create an assignment statement
        val id = "__bp_var${unnestedCount++}"
        val assignment = VariableDeclarationBastNode(id, UNKNOWN, child = ShellStringBastNode(unnestedChildren))

        // create VarDec node
        val variableReference = VariableBastNode(id, UNKNOWN)
        Pair(assignment.toList() + unnestedPreambles, variableReference)
    } else { // current node isn't nested, but children are
        if (this is StatementBastNode) {
            Pair(listOf(), (unnestedPreambles + replaceChildren(unnestedChildren)).toBastNode())
        } else Pair(unnestedPreambles, replaceChildren(listOf(unnestedChildren.toBastNode())))
    }
}

// TODO arithmetic - document
class FinishedBastFactory {

    companion object {
        internal var unnestedCount = 0
        internal val unnestedCountLock = Any()
    }

    private val logger = LogManager.getLogger(Main::javaClass)

    // TODO NOW - make a transformation to flatten ArithmeticBastNodes, move loosen logic here
    // TODO NOW -
    fun transform(root: BastNode): BastNode {
        logger.info("Mermaid graph before unnesting subshells: {}", root.mermaidGraph())
        val unnestedBast = root.unnestSubshells()
        logger.info(
            "Mermaid graph after unnestings subshells, before loose shell strings: {}", unnestedBast.mermaidGraph())
        val looseBast = unnestedBast.loosenShellStrings()
        logger.info(
            "Mermaid graph after loosing shell strings: {}", looseBast.mermaidGraph())
        return looseBast
    }
}