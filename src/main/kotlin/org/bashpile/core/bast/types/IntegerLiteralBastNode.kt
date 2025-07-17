package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.RenderTuple
import java.math.BigInteger

/** Represents an integer of any size */
class IntegerLiteralBastNode(private val bigInt: BigInteger) : BastNode(listOf(), majorType = TypeEnum.INTEGER) {
    override fun render(): RenderTuple {
        return Pair(listOf(), bigInt.toString())
    }

    override fun replaceChildren(nextChildren: List<BastNode>): IntegerLiteralBastNode {
        return IntegerLiteralBastNode(bigInt)
    }
}
