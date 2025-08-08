package org.bashpile.core.bast.statements

import org.bashpile.core.bast.BastNode

/** This is a Print Statement node */
class PrintBastNode(children: List<BastNode> = listOf()) : StatementBastNode(children) {

    constructor(vararg child: BastNode) : this(child.toList())

    /** Combines all children into a single string as a pre-computation for Bash */
    override fun render(): String {
        val childRenders = children.map { it.render() }.joinToString("")
        return "printf \"$childRenders\"\n"
    }

    override fun replaceChildren(nextChildren: List<BastNode>): PrintBastNode {
        return PrintBastNode(nextChildren.map { it.deepCopy() })
    }
}
