package org.bashpile.core.bast.expressions.literals

import org.bashpile.core.TypeEnum
import org.bashpile.core.bast.BastNode
import java.math.BigDecimal

/** Represents any float or double to an arbitrary precision */
class FloatLiteralBastNode(private val float: BigDecimal)
    : BastNode(mutableListOf(), majorType = TypeEnum.FLOAT) {

    override fun render(): String {
        return float.toString()
    }

    override fun replaceChildren(nextChildren: List<BastNode>): FloatLiteralBastNode {
        return FloatLiteralBastNode(float)
    }
}