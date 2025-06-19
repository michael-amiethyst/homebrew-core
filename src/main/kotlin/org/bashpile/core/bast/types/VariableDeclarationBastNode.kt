package org.bashpile.core.bast.types

import org.bashpile.core.Main.Companion.bashpileState
import org.bashpile.core.bast.BastNode

/** Created by [org.bashpile.core.AstConvertingVisitor.visitVariableDeclarationStatement] */
class VariableDeclarationBastNode(
    id: String,
    type: TypeEnum,
    val subtype: TypeEnum = TypeEnum.UNKNOWN,
    val readonly: Boolean = false,
    val export: Boolean = false,
    child: BastNode
) : BastNode(listOf(child), id, type) {
    init {
        bashpileState.addVariableInfo(id, type, subtype, readonly)
    }

    override fun render(): String {
        var exportFlags = ""
        if (export) { exportFlags += "x" }
        val flags = if (exportFlags.isNotEmpty()) "-$exportFlags " else ""
        val childRender = children[0].render()
        return """
            declare $flags$id
            $id="$childRender"
        
        """.trimIndent()
    }
    override fun deepCopy(): VariableDeclarationBastNode {
        return VariableDeclarationBastNode(id!!, majorType, subtype, readonly, export, children[0].deepCopy())
    }
}
