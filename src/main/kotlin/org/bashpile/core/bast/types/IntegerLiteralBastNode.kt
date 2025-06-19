package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode
import java.math.BigInteger

/** Represents an integer of any size */
class IntegerLiteralBastNode(private val bigInt: BigInteger) : BastNode(listOf(), majorType = TypeEnum.INTEGER) {
    override fun render(): String {
        return bigInt.toString()
    }

    override fun deepCopy(): IntegerLiteralBastNode {
        return IntegerLiteralBastNode(bigInt)
    }
}
