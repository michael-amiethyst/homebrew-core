package org.bashpile.core.bast.expressions

import org.bashpile.core.bast.BastNode
import org.bashpile.core.engine.RenderOptions
import org.bashpile.core.engine.TypeEnum

class ArgumentsBastNode(val brackedText: String) : BastNode(mutableListOf(), majorType = TypeEnum.UNKNOWN) {
    override fun render(options: RenderOptions): String {
        return if (brackedText != "all") { "$$brackedText" } else { "$@" }
    }

    override fun replaceChildren(nextChildren: List<BastNode>): BastNode {
        return ArgumentsBastNode(brackedText)
    }
}