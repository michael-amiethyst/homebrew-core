package org.bashpile.core.bast.types.leaf

import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.types.TypeEnum

/** Created by [org.bashpile.core.antlr.AstConvertingVisitor.visitTerminal] */
// TODO 0.16.0 -- unify Leaf and Literal concepts, flatten this package and rename this class to `LiteralBastNode`.  Move TypeEnum, VariableRefereceBastNode and VariableTypeInfo to other packages
open class LeafBastNode(private val text: String, majorType: TypeEnum)
    : BastNode(mutableListOf(), majorType = majorType)
{
    override fun render(): String {
        return text
    }

    override fun replaceChildren(nextChildren: List<BastNode>): LeafBastNode {
        return LeafBastNode(text, majorType())
    }
}
