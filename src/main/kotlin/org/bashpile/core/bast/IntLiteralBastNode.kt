package org.bashpile.core.bast

import java.math.BigInteger

/** Represents an integer of any size */
class IntLiteralBastNode(private val bigInt: BigInteger) : BashpileAst(listOf()) {
    override fun render(): String {
        return bigInt.toString()
    }
}