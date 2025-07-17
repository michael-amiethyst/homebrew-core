package org.bashpile.core.bast.types

import org.bashpile.core.Main.Companion.bashpileState
import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.RenderTuple

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

    // TODO unnest - impl, test with triple nested
    override fun render(): RenderTuple {
        var exportFlags = ""
        if (export) { exportFlags += "x" }
        val flags = if (exportFlags.isNotEmpty()) "-$exportFlags " else ""
        val childRender = children[0].render().second
        return Pair(listOf(), """
            declare $flags$id
            $id="$childRender"
        
        """.trimIndent())
    }
    override fun replaceChildren(nextChildren: List<BastNode>): VariableDeclarationBastNode {
        return VariableDeclarationBastNode(id!!, majorType, subtype, readonly, export, nextChildren[0].deepCopy())
    }
}
