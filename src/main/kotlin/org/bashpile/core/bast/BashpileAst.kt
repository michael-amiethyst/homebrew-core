package org.bashpile.core.bast

import org.bashpile.core.AstConvertingVisitor

/**
 * Converts internal data to Bashpile.  Created by [AstConvertingVisitor].
 */
abstract class BashpileAst(protected val children: List<BashpileAst>) {
    abstract fun render(): String
}
