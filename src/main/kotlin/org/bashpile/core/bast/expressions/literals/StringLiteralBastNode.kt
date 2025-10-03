package org.bashpile.core.bast.expressions.literals

import org.bashpile.core.TypeEnum
import org.bashpile.core.bast.BastNode

class StringLiteralBastNode(val text: String) : BastNode(mutableListOf(), majorType = TypeEnum.STRING) {
    override fun render(): String {
        return text
    }

    override fun replaceChildren(nextChildren: List<BastNode>): StringLiteralBastNode {
        return StringLiteralBastNode(text)
    }
}