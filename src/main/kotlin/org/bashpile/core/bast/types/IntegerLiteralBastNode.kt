package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode
import java.math.BigInteger

/** Represents an integer of any size */
class IntegerLiteralBastNode(private val bigInt: BigInteger)
    : BastNode(mutableListOf(), majorType = TypeEnum.INTEGER) {

    override fun render(): String {
        return bigInt.toString()
    }

    override fun replaceChildren(nextChildren: List<BastNode>): IntegerLiteralBastNode {
        return IntegerLiteralBastNode(bigInt)
    }
}
