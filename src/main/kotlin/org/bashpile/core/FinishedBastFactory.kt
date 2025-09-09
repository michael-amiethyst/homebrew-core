package org.bashpile.core

import com.google.common.annotations.VisibleForTesting
import org.apache.logging.log4j.LogManager
import org.bashpile.core.antlr.AstConvertingVisitor.Companion.ENABLE_STRICT
import org.bashpile.core.antlr.AstConvertingVisitor.Companion.OLD_OPTIONS
import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.InternalBastNode
import org.bashpile.core.bast.Subshell
import org.bashpile.core.bast.expressions.ArithmeticBastNode
import org.bashpile.core.bast.expressions.LooseShellStringBastNode
import org.bashpile.core.bast.expressions.ShellStringBastNode
import org.bashpile.core.bast.statements.ShellLineBastNode
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
        val unnestedBast = flattenedBast.unnestSubshells()
        logger.info("Mermaid graph ----- subshells unnested: {}", unnestedBast.mermaidGraph())

        // loosen
        val looseBast = unnestedBast.loosenShellStrings()
        logger.info("Mermaid graph - shell strings loosened: {}", looseBast.mermaidGraph())
        return looseBast
    }

    private fun BastNode.unnestSubshells(): BastNode = _unnestSubshells(this)

    /**
     * Returns a list of preambles to support unnesting.
     * @return An unnested version of the input tree.
     * @see /documentation/contributing/unnest.md
     */
    @VisibleForTesting
    @Suppress("functionName")
    fun _unnestSubshells(bast: BastNode): BastNode {
        synchronized(unnestedCountLock) {
            return bast.replaceChildren(bast.children.flatMap { statementNode ->
                val nestedSubshells = statementNode.allNodes().filter {
                    it is Subshell
                }.filter { subshells ->
                    subshells.allParents().any { it is Subshell }
                }
                nestedSubshells.map { nestedSubshell ->
                    val id = "__bp_var${unnestedCount++}"
                    nestedSubshell.replaceWith(VariableBastNode(id, UNKNOWN))
                    VariableDeclarationBastNode(
                        id,
                        UNKNOWN,
                        child = ShellStringBastNode(nestedSubshell.children)
                    )
                } + statementNode
            })
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
        // TODO reimplement
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
}
