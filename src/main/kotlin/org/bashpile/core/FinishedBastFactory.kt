package org.bashpile.core

import com.google.common.annotations.VisibleForTesting
import org.apache.logging.log4j.LogManager
import org.bashpile.core.FinishedBastFactory.Companion.unnestedCount
import org.bashpile.core.FinishedBastFactory.Companion.unnestedCountLock
import org.bashpile.core.antlr.AstConvertingVisitor.Companion.ENABLE_STRICT
import org.bashpile.core.antlr.AstConvertingVisitor.Companion.OLD_OPTIONS
import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.InternalBastNode
import org.bashpile.core.bast.UnnestTuple
import org.bashpile.core.bast.expressions.LooseShellStringBastNode
import org.bashpile.core.bast.expressions.ShellStringBastNode
import org.bashpile.core.bast.statements.ShellLineBastNode
import org.bashpile.core.bast.statements.StatementBastNode
import org.bashpile.core.bast.statements.VariableDeclarationBastNode
import org.bashpile.core.bast.types.TypeEnum.UNKNOWN
import org.bashpile.core.bast.types.VariableBastNode


@VisibleForTesting
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
    fun transform(root: BastNode): BastNode {
        logger.info("Mermaid graph before unnesting subshells: {}", root.mermaidGraph())
        val unnestedBast = root.unnestSubshells()
        logger.info(
            "Mermaid graph after unnestings subshells, before loose shell strings: {}", unnestedBast.mermaidGraph())
        val looseBast = unnestedBast.loosenShellStrings().second
        logger.info(
            "Mermaid graph after loosing shell strings: {}", looseBast.mermaidGraph())
        return looseBast
    }

    /** @return A loosened version of the input tree */
    private fun BastNode.loosenShellStrings(foundLooseShellString: Boolean? = null): Pair<Boolean, BastNode> {
        val startRecursion = foundLooseShellString == null
        if (startRecursion) {
            val looseChildren = children.map { it.loosenShellStrings(false).second }
            check(looseChildren.isNotEmpty() && looseChildren[0] is StatementBastNode) {
                "Loose child[0] was not a statement, was " + looseChildren[0].javaClass }
            return Pair(false, replaceChildren(looseChildren))
        }

        // recursive call

        val looseResult = children.map {
            // terminal case is when children are empty
            it.loosenShellStrings(foundLooseShellString)
        }.fold(Pair(this is LooseShellStringBastNode, InternalBastNode())) { acc, b ->
            Pair(acc.first || b.first,
                acc.second.replaceChildren(acc.second.children + b.second))
        }

        val foundLoose = looseResult.first
        val looseStatement = foundLoose && this is StatementBastNode
        val loosenedChildren = looseResult.second.children
        val bastNode = if (looseStatement) {
            // reenable prior (loose) options and then reenable strict mode
            InternalBastNode(
                ShellLineBastNode("eval \"$${OLD_OPTIONS}\""),
                replaceChildren(loosenedChildren),
                ShellLineBastNode(ENABLE_STRICT))
        } else {
            replaceChildren(loosenedChildren)
        }
        return Pair(foundLoose, bastNode)
    }
}