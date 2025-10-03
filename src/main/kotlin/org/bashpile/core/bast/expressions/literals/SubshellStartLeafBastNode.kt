package org.bashpile.core.bast.expressions.literals

import org.bashpile.core.TypeEnum
import org.bashpile.core.bast.BastNode

class SubshellStartLeafBastNode : LeafBastNode( "${'$'}{", TypeEnum.STRING) {
    override fun replaceChildren(nextChildren: List<BastNode>): SubshellStartLeafBastNode {
        return SubshellStartLeafBastNode()
    }
}