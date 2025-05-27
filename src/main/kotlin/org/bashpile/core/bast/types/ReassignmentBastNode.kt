package org.bashpile.core.bast.types

import org.apache.commons.lang3.StringUtils
import org.bashpile.core.bast.BastNode

/** Created by [org.bashpile.core.AstConvertingVisitor.visitVariableDeclarationStatement] */
class ReassignmentBastNode(
    id: String,
    child: BastNode
) : BastNode(listOf(child), id) {
    override fun render(): String {
        check(children.size == 1)
        val varInfo = variableInfo()
        if (varInfo != null) {
            check(!varInfo.readonly) { "Tried to reassign readonly variable: $id" }

            // TODO assignments - move logic out of render()
            val assignType = children[0].resolvedType()
            check(varInfo.type.coercesTo(assignType)) {
                "Tried to reassign variable of type ${varInfo.type} to $assignType: $id"
            }
        }
        val unQuotedRender = StringUtils.strip(children[0].render(), "\"'")
        return """
            $id="$unQuotedRender"
        
        """.trimIndent()
    }
}