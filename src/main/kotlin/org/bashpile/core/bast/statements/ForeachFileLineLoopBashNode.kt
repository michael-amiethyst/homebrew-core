package org.bashpile.core.bast.statements

import org.bashpile.core.Main
import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.types.TypeEnum.EMPTY
import org.bashpile.core.bast.types.VariableBastNode

/**
 * for(FirstName: string, LastName: string in 'src/test/resources/example.csv')
 */
class ForeachFileLineLoopBashNode(
    children: List<BastNode> = listOf(),
    val filepath: String,
    val columns: List<VariableBastNode>) : BastNode(children.toMutableList()) {

    init {
        require(!columns.map { it.id!! }.any { it.contains("\\s".toRegex())}) {
            "Whitespace not allowed in column names"
        }
        check(filepath.startsWith("\"") || filepath.endsWith("\"")) {
            "Filepath should be quoted"
        }
        columns.forEach {
            Main.bashpileState.addVariableInfo(it.id!!, it.majorType, EMPTY, readonly = true)
        }
    }

    override fun replaceChildren(nextChildren: List<BastNode>): ForeachFileLineLoopBashNode {
        return ForeachFileLineLoopBashNode(children.map { it.deepCopy() }, filepath, columns)
    }

    override fun render(): String {
        val columnNamesJoined = columns.map { it.id }.joinToString(" ")
        val childRenderList = children.map { child ->
            child.render().lines().filter { it.isNotBlank() }.map { "    $it" }.joinToString("\n", postfix = "\n")
        }
        val childRenders = childRenderList.joinToString("").removeSuffix("\n")
        // .trimIndent fails with $childRenders so we need to munge whitespace manually
        return """
            cat $filepath | sed '1d' | sed 's/\r//g' | while IFS=',' read -r $columnNamesJoined; do
            $childRenders
            done

        """.lines().filter { it.isNotBlank() }.map{
            it.removePrefix("            ")
        }.joinToString("\n", postfix = "\n")
    }
}