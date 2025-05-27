package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode

class VariableBastNode(id: String, typeEnum: TypeEnum) : BastNode(listOf(), id, typeEnum) {

    override fun render(): String {
        return "$$id"
    }
}
