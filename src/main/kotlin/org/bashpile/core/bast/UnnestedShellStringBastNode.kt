package org.bashpile.core.bast

class UnnestedShellStringBastNode(children: List<BastNode>) : ShellStringBastNode(children) {
    init {
        require(children.size == 2) { "There should be two children, preamble and body" }
    }

    override fun render(): Pair<List<BastNode>, String> {
        return Pair(listOf(children[0]), children[1].render().second)
    }

    override fun replaceChildren(nextChildren: List<BastNode>): BastNode {
        return UnnestedShellStringBastNode(nextChildren.map { it.deepCopy() })
    }
}