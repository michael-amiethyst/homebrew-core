package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode

/** Created by [org.bashpile.core.AstConvertingVisitor.visitVariableDeclarationStatement] */
class ReassignmentBastNode(
    id: String,
    child: BastNode
) : BastNode(listOf(child), id) {
    init {
        check(children.size == 1)
        val varInfo = variableInfo()
        if (varInfo != null) {
            check(!varInfo.readonly) { "Tried to reassign readonly variable: $id" }

            val assignType = children[0].resolvedMajorType()
            check(varInfo.majorType.coercesTo(assignType)) {
                "Tried to reassign variable of type ${varInfo.majorType} to $assignType: $id"
            }
        }
    }

    override fun render(): String {
        val childRender = children[0].render()
        return """
            $id="$childRender"
        
        """.trimIndent()
    }
}
