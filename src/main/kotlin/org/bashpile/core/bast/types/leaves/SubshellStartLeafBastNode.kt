package org.bashpile.core.bast.types.leaves

import org.bashpile.core.bast.BastNode

class SubshellStartLeafBastNode : LeafBastNode("$(") {
    override fun replaceChildren(nextChildren: List<BastNode>): BastNode {
        return SubshellStartLeafBastNode()
    }

    override fun render(): String {
        throw UnsupportedOperationException("Intermediate node")
    }
}