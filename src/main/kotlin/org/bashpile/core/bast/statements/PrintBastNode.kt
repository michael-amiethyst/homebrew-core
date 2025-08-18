package org.bashpile.core.bast.statements

import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.types.TypeEnum.FLOAT
import org.bashpile.core.bast.types.TypeEnum.INTEGER
import org.bashpile.core.bast.types.TypeEnum.UNKNOWN

/** This is a Print Statement node */
class PrintBastNode(children: List<BastNode> = listOf()) : StatementBastNode(children) {

    constructor(vararg child: BastNode) : this(child.toList())

    /** Combines all children into a single string as a pre-computation for Bash */
    override fun render(): String {
        val childRenders = children.map { it.render() }.joinToString("")

        val number = children.areNumbers()
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

    private fun List<BastNode>.areNumbers(): Boolean {
        val isInteger = find { it.majorType == INTEGER } != null &&
                // will only be integer if all coerce to integers
                map { it.majorType }.fold(UNKNOWN) { acc, n -> acc.fold(n) } == INTEGER

        val isFloat = find { it.majorType == FLOAT } != null &&
                map { it.majorType }.fold(UNKNOWN) { acc, n -> acc.fold(n) } == FLOAT
        return isInteger || isFloat
    }
}
