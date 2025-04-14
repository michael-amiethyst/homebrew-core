package org.bashpile.core.bast

class PrintBastNode(children: List<BashpileAst>) : BashpileAst(children) {

    override fun render(): String {
        val childRenders = children.map { it.render() }.joinToString("")
        return "printf \"$childRenders\\n\""
    }
}
