package org.bashpile.core.bast.statements

import org.bashpile.core.bast.BastNode

/**
 * for(FirstName: string, LastName: string in 'src/test/resources/example.csv')
 */
// TODO make IFS configurable, "withDelimiter" keyword?  Just `with`?
class ForeachFileLineLoopBashNode(
    children: List<BastNode> = listOf(),
    val filepath: String,
    vararg val columnNames: String) : BastNode(children.toMutableList()) {

    init {
        require(!columnNames.any { it.contains("\\s".toRegex()) }) { "Whitespace not allowed in column names" }
    }

    override fun replaceChildren(nextChildren: List<BastNode>): ForeachFileLineLoopBashNode {
        return ForeachFileLineLoopBashNode(children.map { it.deepCopy() }, filepath, *columnNames)
    }

    override fun render(): String {
        val columnNamesJoined = columnNames.joinToString(" ")
        val childRenders = children.map {
            it.render()
        }.joinToString("", prefix = "    ").removeSuffix("\n")
        return """
            cat "$filepath" | while IFS=',' read -r $columnNamesJoined; do
            $childRenders
            done

        """.trimIndent()
    }
}