package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode

class LeafBastNode(private val text: String) : BastNode(listOf()) {
    override fun render(): String {
        return text
    }

    override fun replaceChildren(nextChildren: List<BastNode>): LeafBastNode {
        return LeafBastNode(text)
    }

    fun isSubshellStart(): Boolean {
        return text == "${'$'}("
    }

    fun isSubshellEnd(): Boolean {
        return text == ")"
    }
}