package org.bashpile.core.bast.statements

import org.bashpile.core.bast.BastNode

/** Created by [org.bashpile.core.antlr.AstConvertingVisitor.visitVariableDeclarationStatement] */
class ReassignmentBastNode(id: String, child: BastNode) : StatementBastNode(child, id) {
    init {
        check(children.size == 1)
        val varInfo = variableInfo()
        if (varInfo != null) {
            check(!varInfo.readonly) { "Tried to reassign readonly variable: $id" }

            val assignType = children[0].resolvedMajorType()
            check(varInfo.coercesTo(assignType)) {
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

    override fun replaceChildren(nextChildren: List<BastNode>): ReassignmentBastNode {
        return ReassignmentBastNode(id!!, nextChildren[0].deepCopy())
    }
}
