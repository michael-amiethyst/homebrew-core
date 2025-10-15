package org.bashpile.core.engine

data class RenderOptions(val quoted: Boolean = false, val ignoreOutput: Boolean = false) {
    companion object {
        val IGNORE_OUTPUT = RenderOptions(ignoreOutput = true)
        val UNQUOTED = RenderOptions(quoted = false)
        val QUOTED = RenderOptions(quoted = true)
    }
}