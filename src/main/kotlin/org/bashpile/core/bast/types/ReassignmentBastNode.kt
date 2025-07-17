package org.bashpile.core.bast.types

import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.RenderTuple


// TODO move out of types subpackage
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

    override fun render(): RenderTuple {
        val render = children[0].render()
        val stringRender = render.second
        return Pair(render.first, """
            $id="$stringRender"
        
        """.trimIndent())
    }

    override fun replaceChildren(nextChildren: List<BastNode>): ReassignmentBastNode {
        return ReassignmentBastNode(id!!, nextChildren[0].deepCopy())
    }
}
