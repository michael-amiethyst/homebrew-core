package org.bashpile.core.bast

import org.bashpile.core.BashpileVisitor

/** Created by [BashpileVisitor] */
abstract class BashpileAst(protected val children: PrintBastNode?) {
    abstract fun render(): String
}
