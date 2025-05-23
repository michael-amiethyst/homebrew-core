package org.bashpile.core.bast.types

import org.apache.commons.lang3.StringUtils
import org.bashpile.core.bast.BastNode

/** Created by [org.bashpile.core.AstConvertingVisitor.visitVariableDeclarationStatement] */
class VariableDeclarationBastNode(
    val id: String,
    val type: TypeEnum,
    val subtype: TypeEnum = TypeEnum.UNKNOWN,
    val readonly: Boolean = false,
    val export: Boolean = false,
    child: BastNode
) : BastNode(listOf(child)) {
    override fun render(): String {
        var exportFlags = ""
        stackframe.add(VariableTypeInfo(id, type, subtype, readonly))
        if (export) { exportFlags += "x" }
        val flags = if (exportFlags.isNotEmpty()) "-$exportFlags " else ""
        val unQuotedRender = StringUtils.strip(children[0].render(), "\"'")
        return """
            declare $flags$id
            $id="$unQuotedRender"
        
        """.trimIndent()
    }
}