package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode

class VariableBastNode(private val text: String, typeEnum: TypeEnum) : BastNode(listOf(), typeEnum) {

    override fun render(): String {
        return "$$text"
    }
}
