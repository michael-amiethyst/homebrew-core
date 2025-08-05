package org.bashpile.core.bast.types.leaf

import org.bashpile.core.bast.BastNode

class SubshellStartLeafBastNode : LeafBastNode( SUBSHELL_START) {
    companion object {
        @JvmStatic
        val SUBSHELL_START = "$("
    }

    override fun replaceChildren(nextChildren: List<BastNode>): SubshellStartLeafBastNode {
        return SubshellStartLeafBastNode()
    }
}