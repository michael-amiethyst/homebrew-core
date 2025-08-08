package org.bashpile.core.bast.statements

import org.apache.commons.lang3.Strings
import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.types.leaf.LeafBastNode

/** Represents a line of Bash, has no loose mode like [org.bashpile.core.bast.expressions.ShellStringBastNode] */
class ShellLineBastNode(children: List<BastNode> = listOf()) : StatementBastNode(children) {

    constructor(contents: String) : this(LeafBastNode(contents).toList())

    override fun render(): String {
        val childRenders = children.map { it.render() }.joinToString("")
        return childRenders.appendIfMissing("\n")
    }

    override fun replaceChildren(nextChildren: List<BastNode>): BastNode {
        return ShellLineBastNode(nextChildren.map { it.deepCopy() } )
    }

    private fun String.appendIfMissing(suffix: String): String {
        return Strings.CS.appendIfMissing(this, suffix)
    }
}
