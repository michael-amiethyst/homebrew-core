package org.bashpile.core.bast.statements

import org.bashpile.core.Main
import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.types.TypeEnum.EMPTY
import org.bashpile.core.bast.types.VariableReferenceBastNode

/**
 * for(FirstName: string, LastName: string in 'src/test/resources/example.csv')
 */
class ForeachFileLineLoopBashNode(
    children: List<BastNode> = listOf(),
    val doubleQuotedfilepath: String,
    val columns: List<VariableReferenceBastNode>) : BastNode(children.toMutableList()) {

    init {
        require(!columns.map { it.id!! }.any { it.contains("\\s".toRegex())}) {
            "Whitespace not allowed in column names"
        }
        check(doubleQuotedfilepath.startsWith("\"") || doubleQuotedfilepath.endsWith("\"")) {
            "Filepath should be quoted"
        }
    }

    override fun replaceChildren(nextChildren: List<BastNode>): ForeachFileLineLoopBashNode {
        return ForeachFileLineLoopBashNode(children.map { it.deepCopy() }, doubleQuotedfilepath, columns)
    }

    override fun render(): String {
        Main.bashpileState.use { state ->
            state.pushStackframe()
            columns.forEach {
                Main.bashpileState.addVariableInfo(it.id!!, it.majorType, EMPTY, readonly = true)
            }

            val columnNamesJoined = columns.map { it.id }.joinToString(" ")
            val childRenderList = children.map { child ->
                child.render().lines().filter { it.isNotBlank() }.map {
                    "    $it"
                }.joinToString("\n", postfix = "\n")
            }
            val childRenders = childRenderList.joinToString("").removeSuffix("\n")
            val ifs = if (doubleQuotedfilepath.endsWith(".csv\"")) "," else ""
            return """
                cat $doubleQuotedfilepath | ${mungeStream()} | while IFS='$ifs' read -r $columnNamesJoined; do
                $childRenders
                done
    
            """.trimScriptIndent("                ")
        }
    }

    private fun mungeStream(): String {
        // 1 (line) delete
        val skipFirstLine = if (doubleQuotedfilepath.endsWith(".csv\"")) "-e '1d' " else ""
        // remove all '\r'
        val convertWindowsLineEndings = "-e 's/\\r//g'"

        // -z causes '$' to means EOF, not end of line.
        // for files that do not end with a newline ('/\n$/!s') replace EOF with \n EOF
        val appendTrailingNewline = """
                -ze '/\n$/!s/$/\n$/g'""".trimIndent()

        // need to have two gsed calls due to the -z option
        return "gsed ${skipFirstLine}${convertWindowsLineEndings} | gsed $appendTrailingNewline"
    }

    /** .trimIndent fails with $childRenders so we need to munge whitespace manually */
    private fun String.trimScriptIndent(trim: String) = this.lines().filter { it.isNotBlank() }.map {
        it.removePrefix(trim)
    }.joinToString("\n", postfix = "\n")
}