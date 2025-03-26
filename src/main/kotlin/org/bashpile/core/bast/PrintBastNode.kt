package org.bashpile.core.bast

class PrintBastNode(children: BashpileAst) : BashpileAst(listOf(children)) {


    override fun render(): String {
        return children.map { it.render() }.joinToString("") + "\n"
    }
}