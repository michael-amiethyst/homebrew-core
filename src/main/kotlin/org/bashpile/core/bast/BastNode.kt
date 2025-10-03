package org.bashpile.core.bast

import org.bashpile.core.Main.Companion.callStack
import org.bashpile.core.antlr.AstConvertingVisitor
import org.bashpile.core.bast.types.TypeEnum
import org.bashpile.core.bast.types.TypeEnum.UNKNOWN
import org.bashpile.core.bast.types.VariableTypeInfo
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

    // misc methods

    fun toList(): List<BastNode> = listOf(this)

    /**
     * Should be just string manipulation to make final Bashpile text, no logic.
     */
    open fun render(): String {
        return children.joinToString("") { it.render() }
    }

    /**
     * Are all the leaves of the AST string literals?
     * Used to ensure that string concatenation is possible.
     */
    fun areAllStrings(): Boolean {
        return if (children.isEmpty()) {
            this.coercesTo(TypeEnum.STRING)
        } else {
            children.all { it.areAllStrings() }
        }
    }

    fun deepCopy(): BastNode {
        return replaceChildren(this.children)
    }

    /** Depth-first recursive collection of parents (path from root to node) */
    fun allParents(parentsList: List<BastNode> = listOf()): List<BastNode> {
        return if (parent != null) {
            parent!!.allParents(parentsList) + parent
        } else {
            emptyList()
        }.filter { it != null }.map { it!! }
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
    fun all(): Set<BastNode> {
        val childrenSet: MutableSet<BastNode> = mutableSetOf(this)
        childrenSet.addAll(children)
        childrenSet.addAll(children.flatMap { it.all() })
        return childrenSet
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
        val siblings = parent!!.mutableChildren
        val nestedIndex = siblings.indexOf(this)
        check (nestedIndex >= 0) { "Not found" }
        replacement.parent = parent
        siblings[nestedIndex] = replacement
        return this
    }
}
