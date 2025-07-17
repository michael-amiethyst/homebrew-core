package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.RenderTuple
import java.math.BigDecimal

/** Represents any float or double to an arbitrary precision */
class FloatLiteralBastNode(private val float: BigDecimal) : BastNode(listOf(), majorType = TypeEnum.FLOAT) {
    override fun render(): RenderTuple {
        return Pair(listOf(), float.toString())
    }

    override fun replaceChildren(nextChildren: List<BastNode>): FloatLiteralBastNode {
        return FloatLiteralBastNode(float)
    }
}
