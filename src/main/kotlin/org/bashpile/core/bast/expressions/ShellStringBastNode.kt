package org.bashpile.core.bast.expressions

import org.bashpile.core.bast.BastNode
import org.bashpile.core.Subshell
import org.bashpile.core.TypeEnum
import org.bashpile.core.TypeEnum.STRING
import org.bashpile.core.bast.expressions.literals.LeafBastNode

/**
 * A Shell String is the Bashpile equivalent of a Bash subshell (i.e., $() syntax).  It represents an expression.
 * A full line of bash is a [org.bashpile.core.bast.statements.ShellLineBastNode].
 */
open class ShellStringBastNode(children: List<BastNode> = listOf(), majorType: TypeEnum = STRING)
    : BastNode(children.toMutableList(), majorType = majorType), Subshell
{
    constructor(contents: String) : this(LeafBastNode(contents, STRING).asList())

    override fun render(): String {
        val childRenders = children.map { it.render() }.joinToString("")
        return "$($childRenders)"
    }

    override fun replaceChildren(nextChildren: List<BastNode>): ShellStringBastNode {
        return ShellStringBastNode(nextChildren.map { it.deepCopy() }, majorType())
    }
}
