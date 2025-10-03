package org.bashpile.core.bast.types.leaf

import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.types.TypeEnum

class SubshellStartLeafBastNode : LeafBastNode( "${'$'}{", TypeEnum.STRING) {
    override fun replaceChildren(nextChildren: List<BastNode>): SubshellStartLeafBastNode {
        return SubshellStartLeafBastNode()
    }
}
