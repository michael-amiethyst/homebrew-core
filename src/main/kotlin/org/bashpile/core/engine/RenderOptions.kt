package org.bashpile.core.engine

data class RenderOptions(
    val quoted: Boolean = false, val ignoreOutput: Boolean = false, val integerArithmeticContext: Boolean = false)
{
    companion object {
        val IGNORE_OUTPUT = RenderOptions(ignoreOutput = true)
        val UNQUOTED = RenderOptions(quoted = false)
        val QUOTED = RenderOptions(quoted = true)
        val INTEGER_ARITHMETIC = RenderOptions(integerArithmeticContext = true)
    }

    fun quoted(): RenderOptions =
        RenderOptions(quoted = true, ignoreOutput = ignoreOutput, integerArithmeticContext = integerArithmeticContext)
}
