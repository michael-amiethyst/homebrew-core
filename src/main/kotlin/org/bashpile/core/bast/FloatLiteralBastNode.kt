package org.bashpile.core.bast

import java.math.BigDecimal

/** Represents any float or double to an arbitrary precision */
class FloatLiteralBastNode(private val float: BigDecimal) : BashpileAst(listOf()) {
    override fun render(): String {
        return float.toString()
    }
}
