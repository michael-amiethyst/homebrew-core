package org.bashpile.core.bast.statements

import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.expressions.ShellStringBastNode

class ConditionalBastNode(val conditions: List<BastNode>, val blockBodies: List<List<BastNode>>)
    : StatementBastNode(mutableListOf())
{
    init {
        // conditions may only be equal to or one less than blockBodies
        require(conditions.size <= blockBodies.size)
        require(conditions.size >= blockBodies.size - 1)
    }

    override fun replaceChildren(nextChildren: List<BastNode>): BastNode {
        return ConditionalBastNode(conditions.map { it.deepCopy() }, blockBodies.map { statements ->
            statements.map { it.deepCopy()}
        })
    }

    override fun render(): String {
        val formattedBodiesRenders = blockBodies.map { block ->
            block.joinToString("\n") { "    " + it.render() }.removeSuffix("\n")
        }
        val ifCondition = conditions.first()
        val renderedIfCondition = if (ifCondition is ShellStringBastNode) {
            ifCondition.renderRaw()
        } else { "[ ${ifCondition.render()} ]" }
        val renderedIfBody = formattedBodiesRenders.first()
        return when (formattedBodiesRenders.size) {
            1 -> { """
                if $renderedIfCondition; then
                $renderedIfBody
                fi
                
                """.trimScriptIndent("                ")
            }
            2 -> {
                val renderedElseBody = formattedBodiesRenders.last()
                """
                if $renderedIfCondition; then
                $renderedIfBody
                else
                $renderedElseBody
                fi
                
                """.trimScriptIndent("                ")
            }
            else -> {
                // not first or last
                val elseIfs: List<String> = formattedBodiesRenders.subList(1, formattedBodiesRenders.size - 1)
                val renderedElseIfBlocks: String = elseIfs.mapIndexed { index, it ->
                    // offset by one to skip the initial if condition
                    // TODO take brackets off of renderedElseIfCondition
                    val renderedElseIfCondition = conditions[index + 1].render()
                    """
                    elif [ $renderedElseIfCondition ]; then
                    $it
                    """.trimScriptIndent("                    ")
                }.joinToString("\n")
                val renderedElseBody = formattedBodiesRenders.last()
                """
                if $renderedIfCondition; then
                $renderedIfBody
                $renderedElseIfBlocks
                else
                $renderedElseBody
                fi
                
                """.trimScriptIndent("                ")
            }
        }
    }
}