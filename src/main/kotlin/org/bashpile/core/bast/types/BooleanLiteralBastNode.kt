package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode

/** May be true or false, not null */
class BooleanLiteralBastNode(private val bool: Boolean) : BastNode(listOf(), type = TypeEnum.BOOLEAN) {
    override fun render(): String {
        return bool.toString()
    }
}
