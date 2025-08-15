package org.bashpile.core.bast.statements

import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.types.TypeEnum

/** This is a Print Statement node */
class PrintBastNode(children: List<BastNode> = listOf()) : StatementBastNode(children) {

    constructor(vararg child: BastNode) : this(child.toList())

    /** Combines all children into a single string as a pre-computation for Bash */
    override fun render(): String {
        val childRenders = children.map { it.render() }.joinToString("")
        // will only be integer if all integers
        val type = children.map { it.majorType }.fold(TypeEnum.UNKNOWN) { acc, n -> acc.fold(n)}
        val number = type == TypeEnum.INTEGER || type == TypeEnum.FLOAT
        return if (!number) {
            "printf \"$childRenders\"\n"
        } else {
            // treat as a String so floating point numbers work
            """
                printf "%s" "$childRenders"
                
            """.trimIndent()
        }
    }

    override fun replaceChildren(nextChildren: List<BastNode>): PrintBastNode {
        return PrintBastNode(nextChildren.map { it.deepCopy() })
    }
}
