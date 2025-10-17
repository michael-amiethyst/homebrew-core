package org.bashpile.core.bast

import org.bashpile.core.Main.Companion.callStack
import org.bashpile.core.engine.TypeEnum
import org.bashpile.core.engine.TypeEnum.UNKNOWN
import org.bashpile.core.engine.VariableTypeInfo
import org.bashpile.core.antlr.AstConvertingVisitor
import org.bashpile.core.engine.HolderNode
import org.bashpile.core.engine.RenderOptions
import java.util.function.Predicate


/**
 * The base class of the BAST class hierarchy.
 * Converts this AST and children to the Bashpile text output via [render].
 * The root is created by the [AstConvertingVisitor].
 */
abstract class BastNode(
    protected val mutableChildren: MutableList<BastNode>,
    val id: String? = null,
    /** The type at creation time (e.g. for literals).  See [callStack] for variable types. */
    private val majorType: TypeEnum = UNKNOWN
) {

    /** Should only be null for the root of the AST */
    var parent: BastNode? = null
        private set

    val children: List<BastNode>
        // shallow copy
        get() = mutableChildren.toList()

    private var mutable = false

    init {
        children.forEach { it.parent = this }
    }

    ///////////////////////
    // type related methods
    ///////////////////////

    fun coercesTo(type: TypeEnum): Boolean = majorType().coercesTo(type)

    fun majorType(): TypeEnum {
        // check call stack, fall back on node's type
        return callStack.variableInfo(id)?.majorType ?: majorType
    }

    fun variableInfo(): VariableTypeInfo {
        check(id != null) { "Tried to get variable info for null variable ID" }
        return callStack.requireOnStack(id)
    }

    ///////////////////////
    // misc methods
    //////////////////////

    /** Converts this tree to a list */
    fun toList(): List<BastNode> {
        return asList() + children.flatMap { it.toList() }
    }

    /** Converts this node to a list of size 1 */
    fun asList(): List<BastNode> = listOf(this)

    /**
     * Should be just string manipulation to make final Bashpile text, no logic.
     */
    open fun render(options: RenderOptions): String {
        return children.joinToString("") { it.render(RenderOptions.UNQUOTED) }
    }

    fun deepCopy(): BastNode {
        return replaceChildren(this.children)
    }

    /** Returns all parents starting from the bottom of the tree */
    fun parents(): List<BastNode> {
        if (parent == null) return emptyList()

        val parents = mutableListOf(parent!!)
        while (parents.last().parent != null) {
            parents.add(parents.last().parent!!)
        }
        return parents.toList()
    }

    /**
     * @param nextChildren Contents will not be modified
     * @return A new instance of a BastNode subclass with the same fields, besides the children
     */
    open fun replaceChildren(nextChildren: List<BastNode>): BastNode {
        // making this abstract triggers a compilation bug in Ubuntu as of July 2025
        throw UnsupportedOperationException("Should be overridden in child class")
    }

    /** All nodes in this subtree */
    fun allDescendants(): Set<BastNode> {
        val childrenSet: MutableSet<BastNode> = mutableSetOf(this)
        childrenSet.addAll(children)
        childrenSet.addAll(children.flatMap { it.allDescendants() })
        return childrenSet
    }

    /**
     * Like getting [children] but flattens any [HolderNode]s. It ignores [HolderNode]s in favor of their children.
     */
    fun immediateImportantDescendants(): List<BastNode> {
        var ret = mutableChildren
        while (ret.any { it is HolderNode }) {
            ret = ret.flatMap { it.mutableChildren }.toMutableList()
        }
        return ret
    }

    /** Returns true if any node in this subtree matches [condition] */
    fun any(condition: Predicate<BastNode>) : Boolean {
        return condition.test(this) || children.filter { it.any(condition) }.isNotEmpty()
    }

    // mutation related methods

    /** Makes this node mutable */
    fun thaw(): BastNode {
        mutable = true
        return this
    }

    fun freeze(): BastNode {
        mutable = false
        return this
    }

    /** Mutates the children list of parent */
    fun replaceWith(replacement: BastNode): BastNode {
        check (mutable) { "Cannot use mutating call on frozen node, call thaw() first" }
        check (parent != null) { "Cannot be called on root node" }

        replacement.parent = parent

        val myGeneration = parent!!.mutableChildren
        myGeneration[myGeneration.indexOf(this)] = replacement
        return parent!!
    }

    // extension methods

    /** .trimIndent fails with $childRenders so we need to munge whitespace manually */
    protected fun String.trimScriptIndent(trim: String) = this.lines().filter { it.isNotBlank() }.map {
        it.removePrefix(trim)
    }.joinToString("\n", postfix = "\n")
}
