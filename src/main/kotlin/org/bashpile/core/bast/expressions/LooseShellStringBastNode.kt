package org.bashpile.core.bast.expressions

import org.bashpile.core.bast.BastNode

class LooseShellStringBastNode(children: List<BastNode> = listOf()) : ShellStringBastNode(children) {
    override fun replaceChildren(nextChildren: List<BastNode>): LooseShellStringBastNode {
        return LooseShellStringBastNode(nextChildren.map { it.deepCopy() } )
    }
}
