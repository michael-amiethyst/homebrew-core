package org.bashpile.core.bast


/** A Shell String is the Bashpile equivalent of a Bash subshell */
open class ShellStringBastNode(children: List<BastNode>) : BastNode(children) {
    override fun render(): String {
        val childRenders = children.map { it.render() }.joinToString("")
        return "$($childRenders)"
    }

    override fun replaceChildren(nextChildren: List<BastNode>): BastNode {
        return ShellStringBastNode(nextChildren.map { it.deepCopy() })
    }
}
