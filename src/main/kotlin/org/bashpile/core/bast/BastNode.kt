package org.bashpile.core.bast

import org.bashpile.core.AstConvertingVisitor
import org.bashpile.core.AstConvertingVisitor.Companion.ENABLE_STRICT
import org.bashpile.core.AstConvertingVisitor.Companion.OLD_OPTIONS
import org.bashpile.core.Main.Companion.bashpileState
import org.bashpile.core.bast.expressions.LooseShellStringBastNode
import org.bashpile.core.bast.expressions.ShellStringBastNode
import org.bashpile.core.bast.statements.PrintBastNode
import org.bashpile.core.bast.statements.ReassignmentBastNode
import org.bashpile.core.bast.statements.ShellLineBastNode
import org.bashpile.core.bast.statements.VariableDeclarationBastNode
import org.bashpile.core.bast.types.*
import org.bashpile.core.bast.types.TypeEnum.UNKNOWN
import org.bashpile.core.bast.types.leaf.LeafBastNode
import java.util.function.Predicate


typealias UnnestTuple = Pair<List<BastNode>, BastNode>

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
    val majorType: TypeEnum = UNKNOWN
) {
    companion object {
        private var unnestedCount = 0
        private val unnestedCountLock = Any()
        private var mermaidNodeId = 0
        private val mermaidNodeIdLock = Any()
    }

    fun resolvedMajorType(): TypeEnum {
        // check call stack, fall back on node's type
        return bashpileState.variableInfo(id)?.majorType ?: majorType
    }

    fun variableInfo(): VariableTypeInfo? {
        return bashpileState.variableInfo(id)
    }

    fun isStatementNode(): Boolean {
        return this is PrintBastNode || this is ShellLineBastNode || this is VariableDeclarationBastNode
                || this is ReassignmentBastNode
    }

    fun isSubshellNode(): Boolean {
        return this is ShellLineBastNode || this is ShellStringBastNode
    }

    /**
     * Should be just string manipulation to make final Bashpile text, no logic.
     */
    open fun render(): String {
        return children.joinToString("") { it.render() }
    }

    fun mermaidGraph(): String {
        synchronized(mermaidNodeIdLock) {
            mermaidNodeId = 0
            return "graph TD;" + mermaidGraph("root")
        }
    }

    private fun mermaidGraph(parentNodeName: String): String {
        var mermaid = ""
        children.forEach { child ->
            val nodeName = child::class.simpleName!!.removeSuffix("BastNode") + mermaidNodeId++
            mermaid += "$parentNodeName --> $nodeName;${child.mermaidGraph(nodeName)}"
        }
        return mermaid
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
    open fun replaceChildren(nextChildren: List<BastNode>): BastNode {
        // making this abstract triggers a compilation bug in Ubuntu as of July 2025
        throw UnsupportedOperationException("Should be overridden in child class")
    }

    /** @return An unnested version of the input tree */
    fun unnestSubshells(): BastNode {
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
    private fun unnestSubshells(inSubshell: Boolean): UnnestTuple {
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
            Pair(listOf(assignment) + unnestedPreambles, variableReference)
        } else { // current node isn't nested, but children are
            if (isStatementNode()) {
                Pair(listOf(), (unnestedPreambles + replaceChildren(unnestedChildren)).toBastNode())
            } else Pair(unnestedPreambles, replaceChildren(listOf(unnestedChildren.toBastNode())))
        }
    }

    fun findInTree(condition: Predicate<BastNode>) : Boolean {
        return condition.test(this) || children.filter { it.findInTree(condition) }.isNotEmpty()
    }

    /** @return A loosened version of the input tree */
    fun loosenShellStrings(): BastNode {
        val looseChildren = children.map { it.loosenShellStrings(false).second }
        check(looseChildren.isNotEmpty() && looseChildren[0].isStatementNode()) {
            "Loose child[0] was not a statement, was " + looseChildren[0].javaClass }
        return replaceChildren(looseChildren)
    }

    /**
     * Returns a list of preambles to support unnesting.
     * @return Preambles and unnested subshell.
     * @see /documentation/contributing/unnest.md
     */
    private fun loosenShellStrings(foundLooseShellString: Boolean): Pair<Boolean, BastNode> {
        val foundLoose = children.map {
            it.loosenShellStrings(foundLooseShellString)
        }.fold(Pair(this is LooseShellStringBastNode, InternalBastNode())) { acc, b ->
            Pair(acc.first || b.first, acc.second.replaceChildren(acc.second.children + b.second)) }
        return if (foundLoose.first && isStatementNode()) {
            Pair(true, InternalBastNode(
                ShellLineBastNode("eval \"$${OLD_OPTIONS}\""),
                replaceChildren(foundLoose.second.children),
                ShellLineBastNode(ENABLE_STRICT)))
        } else {
            Pair(foundLoose.first, replaceChildren(foundLoose.second.children))
        }
    }

    protected fun List<BastNode>.toBastNode(): BastNode {
        require(isNotEmpty())
        val separator = if (isStatementNode()) "" else " "
        return if (size == 1) first() else InternalBastNode(this, separator)
    }
}
