package org.bashpile.core.bast.expressions

import org.bashpile.core.TypeEnum
import org.bashpile.core.bast.BastNode

class UnaryPrimaryBastNode(private val operator: String, private val rightExpression: BastNode)
    : BastNode(mutableListOf(rightExpression), majorType = TypeEnum.BOOLEAN) {

    companion object {
        val BASH_UNARY_OPERATORS = listOf(
            "-a", "-b", "-c", "-d", "-e", "-f", "-g", "-h", "-k", "-n", "-o", "-p", "-r", "-s", "-t", "-u", "-v", "-w", "-x", "-z",
            "-G", "-L", "-N", "-O", "-R", "-S")
    }

    override fun render(): String {
        val rightRendered = if (rightExpression is VariableReferenceBastNode) {
            "\"${rightExpression.render()}\""
        } else { rightExpression.render() }
        val bashOperator = when (operator) {
            // string operators
            "isEmpty" -> "-z"
            "isNotEmpty" -> "-n"
            // file operators
            "exists" -> "-e"
            "doesNotExist" -> "! -e"
            else -> {
                // accept any Bash unary operator
                check(BASH_UNARY_OPERATORS.contains(operator)) { "Unknown unary operator: $operator" }
                operator
            }
        }
        return "[ $bashOperator $rightRendered ]"
    }

    override fun replaceChildren(nextChildren: List<BastNode>): BastNode {
        return UnaryPrimaryBastNode(operator, rightExpression.deepCopy())
    }
}
