package org.bashpile.core.bast

import org.bashpile.core.antlr.AstConvertingVisitor
import org.bashpile.core.Main.Companion.bashpileState
import org.bashpile.core.bast.expressions.ShellStringBastNode
import org.bashpile.core.bast.statements.ShellLineBastNode
import org.bashpile.core.bast.types.*
import org.bashpile.core.bast.types.TypeEnum.UNKNOWN
import org.bashpile.core.bast.types.leaf.LeafBastNode
import java.util.function.Predicate


typealias UnnestTuple = Pair<List<BastNode>, BastNode>

/**
 * The base class of the BAST class hierarchy.
 * Converts this AST and children to the Bashpile text output via [render].
 * The root is created by the [AstConvertingVisitor].
 * Sometimes the type of a node isn't known at creation time, so the type is on the call stack at [org.bashpile.core.BashpileState].
 */
abstract class BastNode(
    val children: List<BastNode>,
    val id: String? = null,
    /** The type at creation time see class KDoc for more info */
    val majorType: TypeEnum = UNKNOWN
) {
    companion object {
        private var mermaidNodeIds = HashMap<String, Int>()
        private val mermaidNodeIdsLock = Any()
    }

    fun resolvedMajorType(): TypeEnum {
        // check call stack, fall back on node's type
        return bashpileState.variableInfo(id)?.majorType ?: majorType
    }

    fun variableInfo(): VariableTypeInfo? {
        return bashpileState.variableInfo(id)
    }

    fun isSubshellNode(): Boolean {
        return this is ShellLineBastNode || this is ShellStringBastNode
    }

    fun toList(): List<BastNode> = listOf(this)

    /**
     * Should be just string manipulation to make final Bashpile text, no logic.
     */
    open fun render(): String {
        return children.joinToString("") { it.render() }
    }

    fun mermaidGraph(parentNodeName: String = ""): String {
        synchronized(mermaidNodeIdsLock) {
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
     * @param nextChildren Contents will not be modified
     * @return A new instance of a BastNode subclass with the same fields, besides the children
     */
    open fun replaceChildren(nextChildren: List<BastNode>): BastNode {
        // making this abstract triggers a compilation bug in Ubuntu as of July 2025
        throw UnsupportedOperationException("Should be overridden in child class")
    }

    fun findInTree(condition: Predicate<BastNode>) : Boolean {
        return condition.test(this) || children.filter { it.findInTree(condition) }.isNotEmpty()
    }
}
