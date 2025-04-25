package org.bashpile.core.bast


/** This is a Print Statement node */
class PrintBastNode(children: List<BashpileAst>) : BashpileAst(children) {
    /** Combines all children into a single string as a pre-computation for Bash */
    override fun render(): String {
        val childRenders = children.map { it.render() }.joinToString("")
        return "printf \"$childRenders\\n\""
    }
}
