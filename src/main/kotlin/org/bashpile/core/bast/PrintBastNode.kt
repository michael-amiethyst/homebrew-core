package org.bashpile.core.bast


/** This is a Print Statement node */
class PrintBastNode(children: List<BastNode> = listOf()) : BastNode(children) {
    /** Combines all children into a single string as a pre-computation for Bash */
    override fun render(): RenderTuple {
        val renders: List<RenderTuple> = children.map { it.render() }

        val preambles: String = renders.flatMap { it.first }.map {
            val r = it.render()
            require(r.first.isEmpty())
            r.second
        }.joinToString(separator = "")
        val childRenders: String = renders.map { it.second }.joinToString("")

        return Pair(listOf(), "${preambles}printf \"$childRenders\"\n")
    }

    override fun replaceChildren(nextChildren: List<BastNode>): PrintBastNode {
        return PrintBastNode(nextChildren.map { it.deepCopy() })
    }
}
