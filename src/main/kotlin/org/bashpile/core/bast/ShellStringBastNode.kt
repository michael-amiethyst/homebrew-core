package org.bashpile.core.bast


/** A Shell String is the Bashpile equivalent of a Bash subshell */
class ShellStringBastNode(children: List<BastNode>) : BastNode(children) {
    override fun render(): String {
        val childRenders = children.map { it.render() }.joinToString("")
        return "$($childRenders)"
    }

    override fun deepCopy(): ShellStringBastNode {
        return ShellStringBastNode(children.map { it.deepCopy() })
    }
}
