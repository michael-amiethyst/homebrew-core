package org.bashpile.core

import org.apache.logging.log4j.LogManager
import org.bashpile.core.antlr.AstConvertingVisitor.Companion.ENABLE_STRICT
import org.bashpile.core.antlr.AstConvertingVisitor.Companion.OLD_OPTIONS
import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.InternalBastNode
import org.bashpile.core.bast.Subshell
import org.bashpile.core.bast.UnnestTuple
import org.bashpile.core.bast.expressions.ArithmeticBastNode
import org.bashpile.core.bast.expressions.LooseShellStringBastNode
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
        logger.info("Mermaid graph ---------------- initial: {}", root.mermaidGraph())

        // flatten
        val linkedBast = root.linkChildren()
        val flattenedBast = linkedBast.flattenArithmetic()
        logger.info("Mermaid graph --- arithmetic flattened: {}", flattenedBast.mermaidGraph())

        // unnest
        val unnestedResult = unnestSubshells(flattenedBast)
        check(unnestedResult.first.isEmpty()) { "Preambles should be empty" }
        val unnestedBast = unnestedResult.second
        logger.info("Mermaid graph ----- subshells unnested: {}", unnestedBast.mermaidGraph())

        // loosen
        val looseBast = unnestedBast.loosenShellStrings()
        logger.info("Mermaid graph - shell strings loosened: {}", looseBast.mermaidGraph())
        return looseBast
    }

    private fun BastNode.linkChildren(): BastNode {
        // TODO now - impl
        return this
    }

    /**
     * Returns a list of preambles to support unnesting.
     * @return Preambles and an unnested version of the input tree.
     * @see /documentation/contributing/unnest.md
     */
    // TODO add: if [ "$__bp_exitCode0" -ne 0 ]; then exit "$__bp_exitCode0"; fi
    //if [ "${__bp_subshellReturn0}" -eq 1 ]; then
    //  # print statement, Bashpile line 2
    //  printf -- "true\n"
    //fi
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
                val unnestedRoot = unnestSubshells(bast, bast is Subshell)
                return if (unnestedRoot.first.isEmpty()) {
                    // no unnesting performed
                    unnestedRoot
                } else {
                    Pair(listOf(), InternalBastNode(unnestedRoot.first + unnestedRoot.second))
                }
            }

            // recursive case
            val unnestedChildPairs = bast.children.map {
                unnestSubshells(it, inSubshell || bast is Subshell) }
            val unnestedPreambles = unnestedChildPairs.flatMap { it.first }
            val unnestedChildren = unnestedChildPairs.map { it.second }

            val currentNodeIsNested = inSubshell && bast is Subshell
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
    private fun BastNode.loosenShellStrings(): BastNode {
        // no recursion
        val loosenedStatements = children.map {
            val hasLooseShellStringBastNode = it.allNodes().any { child -> child is LooseShellStringBastNode }
            if (hasLooseShellStringBastNode) {
                InternalBastNode(
                    ShellLineBastNode("eval \"$${OLD_OPTIONS}\""),
                    it,
                    ShellLineBastNode(ENABLE_STRICT))
            } else {
                it
            }
        }
        return replaceChildren(loosenedStatements)
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
            InternalBastNode(flattenedChildren, majorType, " ")
        } else {
            replaceChildren(flattenedChildren)
        }
    }

    private fun List<BastNode>.toBastNode(parent: BastNode): BastNode {
        require(isNotEmpty())
        val separator = if (parent is StatementBastNode) "" else " "
        return if (size == 1) first() else InternalBastNode(this, renderSeparator = separator)
    }
}
