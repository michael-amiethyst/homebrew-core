package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode
import java.math.BigDecimal

/** Represents any float or double to an arbitrary precision */
class FloatLiteralBastNode(private val float: BigDecimal) : BastNode(listOf()) {
    override fun render(): String {
        return float.toString()
    }
}
