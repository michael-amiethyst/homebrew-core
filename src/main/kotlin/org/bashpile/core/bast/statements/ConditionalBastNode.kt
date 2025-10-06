package org.bashpile.core.bast.statements

import org.bashpile.core.bast.BastNode

class ConditionalBastNode(val condition: BastNode, val body: List<BastNode>)
    : StatementBastNode(body.toMutableList())
{
    override fun replaceChildren(nextChildren: List<BastNode>): BastNode {
        return ConditionalBastNode(condition.deepCopy(), nextChildren.map { it.deepCopy() })
    }

    override fun render(): String {
        val renderedBody = body.joinToString("\n") { "    " + it.render() }.removeSuffix("\n")
        return """
            if [ ${condition.render()} ]; then
            $renderedBody
            fi
            
        """.trimIndent()
    }
}