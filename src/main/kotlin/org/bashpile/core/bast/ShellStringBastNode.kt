package org.bashpile.core.bast

import org.bashpile.core.bast.types.LeafBastNode


/**
 * A Shell String is the Bashpile equivalent of a Bash subshell (i.e., $() syntax).  It represents an expression.
 * A full line of bash is a [org.bashpile.core.bast.statements.ShellLineBastNode].
 */
open class ShellStringBastNode(children: List<BastNode> = listOf(), val loose: Boolean = false) : BastNode(children) {

    constructor(contents: String, loose: Boolean = false) : this(listOf(LeafBastNode(contents)), loose)

    override fun render(): String {
        val childRenders = children.map { it.render() }.joinToString("")
        return "$($childRenders)"
    }

    override fun replaceChildren(nextChildren: List<BastNode>): ShellStringBastNode {
        return ShellStringBastNode(nextChildren.map { it.deepCopy() }, loose)
    }
}
