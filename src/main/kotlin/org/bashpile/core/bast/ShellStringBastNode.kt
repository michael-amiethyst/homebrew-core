package org.bashpile.core.bast


/** A Shell String is the Bashpile equivalent of a Bash subshell */
open class ShellStringBastNode(children: List<BastNode>) : BastNode(children) {
    override fun render(): RenderTuple {
        val renders = children.map { it.render() }
        val stringRender = renders.map { it.second }.joinToString("")
        return Pair(renders.flatMap { it.first }, "$($stringRender)")
    }

    override fun replaceChildren(nextChildren: List<BastNode>): BastNode {
        return ShellStringBastNode(nextChildren.map { it.deepCopy() })
    }
}
