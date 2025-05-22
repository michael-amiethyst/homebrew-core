package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode

// TODO assignments - make Enum/class for types, make stackframe for type info
class VariableDeclarationBastNode(
    val id: String, val type: String, val subtype: String, child: BastNode
) : BastNode(listOf(child)) {
    override fun render(): String {
        return """
            declare $id
            $id="${children[0].render()}"
        
        """.trimIndent()
    }
}