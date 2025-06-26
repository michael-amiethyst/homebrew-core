package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode

class StringLiteralBastNode(val text: String) : BastNode(listOf(), majorType = TypeEnum.STRING) {
    override fun render(): Pair<List<BastNode>, String> {
        return Pair(listOf(), text)
    }

    override fun replaceChildren(nextChildren: List<BastNode>): StringLiteralBastNode {
        return StringLiteralBastNode(text)
    }
}
