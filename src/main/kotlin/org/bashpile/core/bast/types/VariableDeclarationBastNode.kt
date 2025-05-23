package org.bashpile.core.bast.types

import org.apache.commons.lang3.StringUtils
import org.bashpile.core.bast.BastNode

// TODO assignments - make Enum/class for types, make stackframe for type info (e.g. readonly)
/** Created by [org.bashpile.core.AstConvertingVisitor.visitVariableDeclarationStatement] */
class VariableDeclarationBastNode(
    val id: String,
    val type: String,
    val subtype: String = "",
    val readonly: Boolean = false,
    val export: Boolean = false,
    child: BastNode
) : BastNode(listOf(child)) {
    override fun render(): String {
        var exportFlags = ""
        // TODO assignments - put readonly to the stackframe
        if (export) { exportFlags += "x" }
        val flags = if (exportFlags.isNotEmpty()) "-$exportFlags " else ""
        val unQuotedRender = StringUtils.strip(children[0].render(), "\"'")
        return """
            declare $flags$id
            $id="$unQuotedRender"
        
        """.trimIndent()
    }
}