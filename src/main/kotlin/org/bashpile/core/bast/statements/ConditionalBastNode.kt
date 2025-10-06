package org.bashpile.core.bast.statements

import org.bashpile.core.bast.BastNode

class ConditionalBastNode(val condition: BastNode, val blockBodies: List<List<BastNode>>)
    : StatementBastNode(mutableListOf())
{
    override fun replaceChildren(nextChildren: List<BastNode>): BastNode {
        return ConditionalBastNode(condition.deepCopy(), blockBodies.map { statements ->
            statements.map { it.deepCopy()}
        })
    }

    override fun render(): String {
        check(blockBodies.size <= 2)
        val renderedIfBody = blockBodies.first().joinToString("\n") { "    " + it.render() }.removeSuffix("\n")
        val renderedElseBody = blockBodies.last().joinToString("\n") { "    " + it.render()  }.removeSuffix("\n")
        return if (blockBodies.size == 1) { """
            if [ ${condition.render()} ]; then
            $renderedIfBody
            fi
            
            """.trimIndent()
        } else { """
            if [ ${condition.render()} ]; then
            $renderedIfBody
            else
            $renderedElseBody
            fi
            
            """.trimIndent()
        }
    }
}