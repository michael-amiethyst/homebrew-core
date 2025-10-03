package org.bashpile.core.bast.statements

import org.bashpile.core.appendIfMissing
import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.Subshell
import org.bashpile.core.bast.types.TypeEnum
import org.bashpile.core.bast.types.leaf.LeafBastNode

/** Represents a line of Bash, has no loose mode like [org.bashpile.core.bast.expressions.ShellStringBastNode] */
class ShellLineBastNode(children: List<BastNode> = listOf()) : StatementBastNode(children), Subshell {

    constructor(contents: String) : this(LeafBastNode(contents, TypeEnum.UNKNOWN).asList())

    override fun render(): String {
        val childRenders = children.map { it.render() }.joinToString("")
        return childRenders.appendIfMissing("\n")
    }

    override fun replaceChildren(nextChildren: List<BastNode>): BastNode {
        return ShellLineBastNode(nextChildren.map { it.deepCopy() })
    }
}
