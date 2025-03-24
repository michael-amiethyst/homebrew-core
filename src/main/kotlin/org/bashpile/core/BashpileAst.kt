package org.bashpile.core

import org.bashpile.core.bast.PrintBastNode

/** Created by [BashpileVisitor] */
abstract class BashpileAst(protected val children: PrintBastNode?) {
    abstract fun render(): String
}
