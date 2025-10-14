package org.bashpile.core.bast.expressions

import org.bashpile.core.bast.BastNode
import org.bashpile.core.Subshell
import org.bashpile.core.TypeEnum
import org.bashpile.core.TypeEnum.STRING
import org.bashpile.core.bast.expressions.literals.TerminalBastNode
import org.bashpile.core.bast.statements.ConditionalBastNode
import org.bashpile.core.engine.RenderOptions

/**
 * A Shell String is the Bashpile equivalent of a Bash subshell (i.e., $() syntax).  It represents an expression.
 * A full line of bash is a [org.bashpile.core.bast.statements.ShellLineBastNode].
 */
open class ShellStringBastNode(children: List<BastNode> = listOf(), majorType: TypeEnum = STRING)
    : BastNode(children.toMutableList(), majorType = majorType), Subshell
{
    constructor(contents: String) : this(TerminalBastNode(contents, STRING).asList())

    override fun render(options: RenderOptions): String {
        val childRenders = children.map { it.render(RenderOptions.UNQUOTED) }.joinToString("")
        // TODO move ignoreOutput into RenderOptions
        val subshell = if (parent is ConditionalBastNode || parent is CombiningExpressionBastNode) {
            childRenders.ignoreOutput()
        } else { "$($childRenders)" }
        return if (options.quoted) { "\"$subshell\"" } else { subshell }
    }

    override fun replaceChildren(nextChildren: List<BastNode>): ShellStringBastNode {
        return ShellStringBastNode(nextChildren.map { it.deepCopy() }, majorType())
    }

    private fun String.ignoreOutput(): String {
        return if (parent is CombiningExpressionBastNode ||
                parent is ConditionalBastNode || parent is BinaryPrimaryBastNode) {
            "($this) >/dev/null 2>&1"
        } else {
            this
        }
    }
}
