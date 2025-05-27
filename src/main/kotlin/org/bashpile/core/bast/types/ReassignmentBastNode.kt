package org.bashpile.core.bast.types

import org.apache.commons.lang3.StringUtils
import org.bashpile.core.Main
import org.bashpile.core.bast.BastNode

/** Created by [org.bashpile.core.AstConvertingVisitor.visitVariableDeclarationStatement] */
class ReassignmentBastNode(
    val id: String,
    child: BastNode
) : BastNode(listOf(child)) {
    override fun render(): String {
        check(children.size == 1)
        val varInfo = Main.instance.stackframe.find { it.id == id }
        if (varInfo != null) {
            check(!varInfo.readonly) { "Tried to reassign readonly variable: $id" }

            // TODO assignments - add "id" field and use here, move logic out of render()
            val reassignmentNode = children[0] as ReassignmentBastNode
            val assignType = Main.instance.stackframe.find { it.id == reassignmentNode.id }?.type ?: TypeEnum.UNKNOWN
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