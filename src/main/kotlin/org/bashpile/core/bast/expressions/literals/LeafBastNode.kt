package org.bashpile.core.bast.expressions.literals

import org.bashpile.core.TypeEnum
import org.bashpile.core.bast.BastNode

/** Created by [org.bashpile.core.antlr.AstConvertingVisitor.visitTerminal] */
// TODO 0.16.0 -- Rename Leaf classes to Terminals
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