package org.bashpile.core.bast.statements

import org.bashpile.core.bast.BastNode
import org.bashpile.core.engine.RenderOptions

/** Created by [org.bashpile.core.antlr.AstConvertingVisitor.visitVariableDeclarationStatement] */
class ReassignmentBastNode(id: String, child: BastNode) : StatementBastNode(child, id) {

    override fun render(options: RenderOptions): String {
        // guard
        val varInfo = variableInfo()
        check(!varInfo.readonly) { "Tried to reassign readonly variable: $id" }
        val assignType = children[0].majorType()
        check(varInfo.coercesTo(assignType)) {
            "Tried to reassign variable of type ${varInfo.majorType} to $assignType: $id"
        }

        // body
        val childRender = children[0].render(RenderOptions.QUOTED)
        return """
            $id=$childRender

        """.trimIndent()
    }

    override fun replaceChildren(nextChildren: List<BastNode>): ReassignmentBastNode {
        return ReassignmentBastNode(id!!, nextChildren[0].deepCopy())
    }
}
