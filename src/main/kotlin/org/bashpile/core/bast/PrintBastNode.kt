package org.bashpile.core.bast

class PrintBastNode(children: List<BashpileAst>) : BashpileAst(children) {

    override fun render(): String {
        return children.map { it.render() }.joinToString("") + "\n"
    }
}