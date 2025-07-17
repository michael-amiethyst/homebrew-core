package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.RenderTuple

class SubshellStartLeafBastNode : LeafBastNode("$(") {
    override fun replaceChildren(nextChildren: List<BastNode>): LeafBastNode {
        return SubshellStartLeafBastNode()
    }

    override fun render(): RenderTuple {
        throw UnsupportedOperationException("Intermediate node")
    }
}