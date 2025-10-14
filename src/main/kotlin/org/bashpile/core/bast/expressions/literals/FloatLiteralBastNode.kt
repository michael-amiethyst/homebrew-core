package org.bashpile.core.bast.expressions.literals

import org.bashpile.core.TypeEnum
import org.bashpile.core.bast.BastNode
import org.bashpile.core.engine.RenderOptions
import java.math.BigDecimal

/** Represents any float or double to an arbitrary precision */
class FloatLiteralBastNode(private val float: BigDecimal)
    : BastNode(mutableListOf(), majorType = TypeEnum.FLOAT), Literal
{
    override fun render(options: RenderOptions): String {
        return float.toString()
    }

    override fun replaceChildren(nextChildren: List<BastNode>): FloatLiteralBastNode {
        return FloatLiteralBastNode(float)
    }
}
