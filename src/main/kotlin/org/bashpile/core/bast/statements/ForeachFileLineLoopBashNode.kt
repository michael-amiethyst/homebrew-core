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
    val doubleQuotedfilepath: String,
    val columns: List<VariableBastNode>) : BastNode(children.toMutableList()) {

    init {
        require(!columns.map { it.id!! }.any { it.contains("\\s".toRegex())}) {
            "Whitespace not allowed in column names"
        }
        check(doubleQuotedfilepath.startsWith("\"") || doubleQuotedfilepath.endsWith("\"")) {
            "Filepath should be quoted"
        }
        columns.forEach {
            Main.bashpileState.addVariableInfo(it.id!!, it.majorType, EMPTY, readonly = true)
        }
    }

    override fun replaceChildren(nextChildren: List<BastNode>): ForeachFileLineLoopBashNode {
        return ForeachFileLineLoopBashNode(children.map { it.deepCopy() }, doubleQuotedfilepath, columns)
    }

    override fun render(): String {
        val columnNamesJoined = columns.map { it.id }.joinToString(" ")
        val childRenderList = children.map { child ->
            child.render().lines().filter { it.isNotBlank() }.map { "    $it" }.joinToString("\n", postfix = "\n")
        }
        val childRenders = childRenderList.joinToString("").removeSuffix("\n")
        // TODO foreach -- use gsed -e -e -e instead of multiple calls to sed
        val skipFirstLine = if (doubleQuotedfilepath.endsWith(".csv\"")) "sed '1d' | " else ""
        val convertWindowsLineEndings = "sed 's/\\r//g' | "
        // -z '$' means EOF, not end of line.
        // for files that do not end with a newline ('/\n$/!s') replace EOF with \n EOF
        val appendTrailingNewline = """
            gsed -z '/\n$/!s/$/\n$/g'""".trimIndent()
        val ifs = if (doubleQuotedfilepath.endsWith(".csv\"")) "," else ""
        // .trimIndent fails with $childRenders so we need to munge whitespace manually
        val mungeStream = "${skipFirstLine}${convertWindowsLineEndings}${appendTrailingNewline}"
        return """
            cat $doubleQuotedfilepath | $mungeStream | while IFS='$ifs' read -r $columnNamesJoined; do
            $childRenders
            done

        """.lines().filter { it.isNotBlank() }.map{
            it.removePrefix("            ")
        }.joinToString("\n", postfix = "\n")
    }
}