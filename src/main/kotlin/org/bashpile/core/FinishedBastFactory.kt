package org.bashpile.core

import com.google.common.annotations.VisibleForTesting
import org.apache.logging.log4j.LogManager
import org.bashpile.core.antlr.AstConvertingVisitor.Companion.ENABLE_STRICT
import org.bashpile.core.antlr.AstConvertingVisitor.Companion.OLD_OPTIONS
import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.InternalBastNode
import org.bashpile.core.bast.expressions.ArithmeticBastNode
import org.bashpile.core.bast.expressions.LooseShellStringBastNode
import org.bashpile.core.bast.expressions.ShellStringBastNode
import org.bashpile.core.bast.statements.ShellLineBastNode
import org.bashpile.core.bast.statements.VariableDeclarationBastNode
import org.bashpile.core.TypeEnum.UNKNOWN
import org.bashpile.core.bast.expressions.VariableReferenceBastNode


/**
 * Takes a freshly created Bashpile Abstract Syntax Tree from [org.bashpile.core.antlr.AstConvertingVisitor] and
 * performs a series of transformations on it to prepare it for rendering with [BastNode.render].
 */
class FinishedBastFactory {

    private val logger = LogManager.getLogger(Main::javaClass)

    fun transform(root: BastNode): BastNode {
        logger.info("Mermaid graph ---------------- initial: {}", root.mermaidGraph())

        // flatten
        val flattenedBast = root.flattenArithmetic()
        logger.info("Mermaid graph --- arithmetic flattened: {}", flattenedBast.mermaidGraph())

        // unnest
        val unnestedBast = flattenedBast.unnestSubshells()
        logger.info("Mermaid graph ----- subshells unnested: {}", unnestedBast.mermaidGraph())

        // loosen
        val looseBast = unnestedBast.loosenShellStrings()
        logger.info("Mermaid graph - shell strings loosened: {}", looseBast.mermaidGraph())
        return looseBast
    }

    @VisibleForTesting
    internal fun BastNode.mermaidGraph(parentNodeName: String = "", mermaidNodeIds: HashMap<String, Int> = HashMap())
    : String {
        if (parentNodeName.isEmpty()) {
            // initial case
            mermaidNodeIds.clear()
            return "graph TD;" + mermaidGraph("root")
        } else {
            // terminating cose: no children
            var mermaid = ""
            children.forEach { child ->
                val nodeTypeName = child::class.simpleName!!.removeSuffix("BastNode")
                val nodeId = mermaidNodeIds.getOrDefault(nodeTypeName, Integer.valueOf(0))
                val nodeName = nodeTypeName + nodeId
                mermaidNodeIds[nodeTypeName] = nodeId + 1
                mermaid += "$parentNodeName --> $nodeName;${child.mermaidGraph(nodeName)}"
            }
            return mermaid
        }
    }

    /**
     * Returns a list of preambles to support unnesting.
     * @return An unnested version of the input tree.
     * @see /documentation/contributing/unnest.md
     */
    @VisibleForTesting
    internal fun BastNode.unnestSubshells(): BastNode {
        var unnestedCount = 0
        val unnestedChildren = children.flatMap { statementNode ->
            // the recursion is hidden in .allNodes(), it's linear from there
            val nestedSubshells = statementNode.all().filter {
                it is Subshell
            }.filter { subshells ->
                subshells.parents().any { it is Subshell }
            }
            val variableDeclarationBastNodes = nestedSubshells.map { nestedSubshell ->
                val id = "__bp_var${unnestedCount++}"
                nestedSubshell.thaw()
                    .replaceWith(VariableReferenceBastNode(id, UNKNOWN))
                    .freeze()
                VariableDeclarationBastNode(
                    id,
                    UNKNOWN,
                    child = ShellStringBastNode(nestedSubshell.children)
                )
            }
            variableDeclarationBastNodes + statementNode
        }
        return replaceChildren(unnestedChildren)
    }

    /** @return A loosened version of the input tree */
    private fun BastNode.loosenShellStrings(): BastNode {
        // no recursion
        val loosenedStatements = children.map {
            val hasLooseShellStringBastNode = it.any { child -> child is LooseShellStringBastNode }
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

    /** Replaces nested arithmetic nodes with internal nodes */
    private fun BastNode.flattenArithmetic(inArithmetic: Boolean = this is ArithmeticBastNode): BastNode {
        val flattenedChildren = children.map {
            it.flattenArithmetic(inArithmetic || this is ArithmeticBastNode)
        }

        // TODO just change render of AritmeticBastNode instead of this method
        return if (inArithmetic && this is ArithmeticBastNode) {
            // with the recursive mapping it maps this nested ArithmeticBastNode to a generic InternalBastNode
            InternalBastNode(flattenedChildren, majorType(), " ")
        } else {
            replaceChildren(flattenedChildren)
        }
    }
}
