package org.bashpile.core

import org.apache.logging.log4j.LogManager
import org.bashpile.core.antlr.AstConvertingVisitor.Companion.ENABLE_STRICT
import org.bashpile.core.antlr.AstConvertingVisitor.Companion.OLD_OPTIONS
import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.InternalBastNode
import org.bashpile.core.bast.UnnestTuple
import org.bashpile.core.bast.expressions.ArithmeticBastNode
import org.bashpile.core.bast.expressions.LooseShellStringBastNode
import org.bashpile.core.bast.expressions.ParenthesisBastNode
import org.bashpile.core.bast.expressions.ShellStringBastNode
import org.bashpile.core.bast.statements.ShellLineBastNode
import org.bashpile.core.bast.statements.StatementBastNode
import org.bashpile.core.bast.statements.VariableDeclarationBastNode
import org.bashpile.core.bast.types.TypeEnum.UNKNOWN
import org.bashpile.core.bast.types.VariableBastNode


/**
 * Takes a freshly created Bashpile Abstract Syntax Tree from [org.bashpile.core.antlr.AstConvertingVisitor] and
 * performs a series of transformations on it to prepare it for rendering with [BastNode.render].
 */
class FinishedBastFactory {

    companion object {
        internal var unnestedCount = 0
        internal val unnestedCountLock = Any()
    }

    private val logger = LogManager.getLogger(Main::javaClass)

    fun transform(root: BastNode): BastNode {
        // unnest
        logger.info("Mermaid graph ---------------- initial: {}", root.mermaidGraph())
        val unnestedResult = unnestSubshells(root.flattenArithmetic())
        check(unnestedResult.first.isEmpty()) { "Preambles should be empty" }
        val unnestedBast = unnestedResult.second
        logger.info("Mermaid graph ----- subshells unnested: {}", unnestedBast.mermaidGraph())

        // loosen
        val looseBast = unnestedBast.loosenShellStrings().second
        logger.info("Mermaid graph - shell strings loosened: {}", looseBast.mermaidGraph())

        // flatten
        val flattenedBast = looseBast.flattenArithmetic()
        logger.info("Mermaid graph --- arithmetic flattened: {}", flattenedBast.mermaidGraph())
        return flattenedBast
    }

    /**
     * Returns a list of preambles to support unnesting.
     * @return Preambles and an unnested version of the input tree.
     * @see /documentation/contributing/unnest.md
     */
    fun unnestSubshells(bast: BastNode, inSubshell: Boolean? = null): UnnestTuple {
        synchronized(unnestedCountLock) {
            val startRecursion = inSubshell == null
            if (startRecursion) {
                unnestedCount = 0
                val hasLooseShellStringChild = bast.findInTree { bast is LooseShellStringBastNode }
                if (hasLooseShellStringChild) {
                    return Pair(listOf(), bast)
                }
                // recursive call
                val unnestedRoot = unnestSubshells(bast, bast.isSubshellNode())
                return if (unnestedRoot.first.isEmpty()) {
                    // no unnesting performed
                    unnestedRoot
                } else {
                    Pair(listOf(), InternalBastNode(unnestedRoot.first + unnestedRoot.second))
                }
            }

            // recursive case
            val unnestedChildPairs = bast.children.map {
                unnestSubshells(it, inSubshell || bast.isSubshellNode()) }
            val unnestedPreambles = unnestedChildPairs.flatMap { it.first }
            val unnestedChildren = unnestedChildPairs.map { it.second }

            val currentNodeIsNested = inSubshell && bast.isSubshellNode()
            val noNestedChildren = unnestedPreambles.isEmpty()
            return if (!currentNodeIsNested && noNestedChildren) {
                Pair(listOf(), bast.replaceChildren(unnestedChildren))
            } else if (currentNodeIsNested) {
                // create an assignment statement
                val id = "__bp_var${unnestedCount++}"
                val assignment =
                    VariableDeclarationBastNode(id, UNKNOWN, child = ShellStringBastNode(unnestedChildren))

                // create VarDec node
                val variableReference = VariableBastNode(id, UNKNOWN)
                Pair(assignment.toList() + unnestedPreambles, variableReference)
            } else { // current node isn't nested, but children are
                if (bast is StatementBastNode) {
                    val joinedBastNodes = unnestedPreambles + bast.replaceChildren(unnestedChildren)
                    Pair(listOf(), joinedBastNodes.toBastNode(bast))
                } else {
                    Pair(unnestedPreambles,
                        bast.replaceChildren(unnestedChildren))
                }
            }
        }
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

    private fun BastNode.flattenArithmetic(inArithmetic: Boolean? = null): BastNode {
        val startRecursion = inArithmetic == null
        if (startRecursion) {
            return replaceChildren(children.map { it.flattenArithmetic(this is ArithmeticBastNode) })
        }

        val needsFlattening = inArithmetic && this is ArithmeticBastNode
        // terminal case is when children are empty
        val flattenedChildren = children.map {
            it.flattenArithmetic(inArithmetic || this is ArithmeticBastNode) }
        return if (needsFlattening) {
            ParenthesisBastNode(flattenedChildren, majorType)
        } else {
            replaceChildren(flattenedChildren)
        }
    }

    private fun List<BastNode>.toBastNode(parent: BastNode): BastNode {
        require(isNotEmpty())
        val separator = if (parent is StatementBastNode) "" else " "
        return if (size == 1) first() else InternalBastNode(this, separator)
    }
}
