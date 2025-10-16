package org.bashpile.core.bast.statements

import org.bashpile.core.bast.BastNode
import org.bashpile.core.TypeEnum.FLOAT
import org.bashpile.core.TypeEnum.INTEGER
import org.bashpile.core.TypeEnum.UNKNOWN
import org.bashpile.core.bast.expressions.ArithmeticBastNode
import org.bashpile.core.engine.RenderOptions

/** This is a Print Statement node */
class PrintBastNode(children: List<BastNode> = listOf()) : StatementBastNode(children) {

    constructor(vararg child: BastNode) : this(child.toList())

    /** Combines all children into a single string as a pre-computation for Bash */
    override fun render(options: RenderOptions): String {
        val childRenders = children.map { it.render(RenderOptions.UNQUOTED) }.joinToString("")

        // TODO 0.19.0 write test with children in parenthesis, use RenderOptions.ARITHMETIC (or .isInArithmeticContext()) instead?
        val number = children.areNumbers()
        val noArithmeticChildren = children.all { it !is ArithmeticBastNode }
        return if (!number && noArithmeticChildren) {
            "printf \"$childRenders\"\n"
        } else {
            // treat as a String so floating point numbers work
            // we do the formatting on the Kotlin layer (we're not using %f for that reason)
            // we need the %s so Bash doesn't interpret negative numbers as options to the program
            """
                printf "%s" "$childRenders"
                
            """.trimIndent()
        }
    }

    override fun replaceChildren(nextChildren: List<BastNode>): PrintBastNode {
        return PrintBastNode(nextChildren.map { it.deepCopy() })
    }

    private fun List<BastNode>.areNumbers(): Boolean {
        val isInteger = any { it.majorType() == INTEGER } &&
                // will only be integer if all coerce to integers
                map { it.majorType() }.fold(UNKNOWN) { acc, n -> acc.fold(n) } == INTEGER

        val isFloat = any { it.majorType() == FLOAT } &&
                map { it.majorType() }.fold(UNKNOWN) { acc, n -> acc.fold(n) } == FLOAT
        return isInteger || isFloat
    }
}
