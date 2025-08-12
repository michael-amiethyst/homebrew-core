package org.bashpile.core.bast

import org.bashpile.core.antlr.AstConvertingVisitor
import org.bashpile.core.antlr.AstConvertingVisitor.Companion.ENABLE_STRICT
import org.bashpile.core.antlr.AstConvertingVisitor.Companion.OLD_OPTIONS
import org.bashpile.core.Main.Companion.bashpileState
import org.bashpile.core.bast.expressions.LooseShellStringBastNode
import org.bashpile.core.bast.expressions.ShellStringBastNode
import org.bashpile.core.bast.statements.ShellLineBastNode
import org.bashpile.core.bast.statements.StatementBastNode
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

    /** @return A loosened version of the input tree */
    fun loosenShellStrings(foundLooseShellString: Boolean? = null): Pair<Boolean, BastNode> {
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

    //////////
    // helpers
    //////////

    fun List<BastNode>.toBastNode(): BastNode {
        require(isNotEmpty())
        val separator = if (this@BastNode is StatementBastNode) "" else " "
        return if (size == 1) first() else InternalBastNode(this, separator)
    }

    fun findInTree(condition: Predicate<BastNode>) : Boolean {
        return condition.test(this) || children.filter { it.findInTree(condition) }.isNotEmpty()
    }
}
