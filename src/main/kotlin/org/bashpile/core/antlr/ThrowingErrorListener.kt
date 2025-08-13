package org.bashpile.core.antlr

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.misc.ParseCancellationException

/**
 * Throws an exception with the same information as the default action of print to STDOUT.
 * Taken from a [Stack Overflow answer](https://stackoverflow.com/a/26573239) and auto-converted to Kotlin.
 */
class ThrowingErrorListener: BaseErrorListener() {
    @Throws(ParseCancellationException::class)
    override fun syntaxError(
        recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPositionInLine: Int,
        msg: String, e: RecognitionException?
    ) {
        throw ParseCancellationException("line $line:$charPositionInLine $msg ${e?.stackTrace ?: ""}")
    }
}
