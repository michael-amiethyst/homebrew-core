package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode
import java.math.BigInteger

/** Represents an integer of any size */
class IntLiteralBastNode(private val bigInt: BigInteger) : BastNode(listOf(), majorType = TypeEnum.INT) {
    override fun render(): String {
        return bigInt.toString()
    }
}
