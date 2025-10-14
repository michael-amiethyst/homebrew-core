package org.bashpile.core.engine

data class RenderOptions(val quoted: Boolean = false) {
    companion object {
        val UNQUOTED = RenderOptions(quoted = false)
        val QUOTED = RenderOptions(quoted = true)
    }
}