package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode

class StringLiteralBastNode(private val text: String) : BastNode(listOf(), majorType = TypeEnum.STRING) {
    override fun render(): String {
        return text
    }
}
