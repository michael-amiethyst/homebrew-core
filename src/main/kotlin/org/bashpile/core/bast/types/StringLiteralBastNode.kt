package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode

class StringLiteralBastNode(val text: String) : BastNode(listOf(), majorType = TypeEnum.STRING) {
    override fun render(): String {
        return text
    }

    override fun deepCopy(): StringLiteralBastNode {
        return StringLiteralBastNode(this.text)
    }
}
