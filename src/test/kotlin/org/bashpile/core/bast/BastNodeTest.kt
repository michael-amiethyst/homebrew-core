package org.bashpile.core.bast

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bashpile.core.AstConvertingVisitor.Companion.OLD_OPTIONS
import org.bashpile.core.Main
import org.bashpile.core.SCRIPT_GENERIC_ERROR
import org.bashpile.core.SCRIPT_SUCCESS
import org.bashpile.core.bast.expressions.LooseShellStringBastNode
import org.bashpile.core.bast.expressions.ShellStringBastNode
import org.bashpile.core.bast.statements.PrintBastNode
import org.bashpile.core.bast.statements.ShellLineBastNode
import org.bashpile.core.bast.types.BooleanLiteralBastNode
import org.bashpile.core.bast.types.IntegerLiteralBastNode
import org.bashpile.core.bast.types.StringLiteralBastNode
import org.bashpile.core.bast.types.leaf.LeafBastNode
import org.bashpile.core.runCommand
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class BastNodeTest {
    val log: Logger = LogManager.getLogger()
    @Test
    fun areAllStrings() {
        val stringLiteral = StringLiteralBastNode("\"hello\"")
        val intLiteral = IntegerLiteralBastNode(1.toBigInteger())
        val booleanLiteral = BooleanLiteralBastNode(true)

        assertTrue(stringLiteral.areAllStrings())
        assertFalse(intLiteral.areAllStrings())
        assertFalse(booleanLiteral.areAllStrings())

        val listOfStringLiterals = InternalBastNode(listOf(stringLiteral, stringLiteral))
        assertTrue(listOfStringLiterals.areAllStrings())

        val listWithIntAndString = InternalBastNode(listOf(stringLiteral, intLiteral))
        assertFalse(listWithIntAndString.areAllStrings())
    }

    @Test
    fun deepCopyWorks() {
        val child = StringLiteralBastNode("Hello")
        val fixture = InternalBastNode(listOf(child))

        val origChild = fixture.children[0] as StringLiteralBastNode
        assertEquals(fixture.deepCopy().children.size, 1)
        val copyChild = fixture.deepCopy().children[0] as StringLiteralBastNode

        assertEquals(origChild.text, copyChild.text)
        assertNotEquals(origChild.toString(), copyChild.toString())
    }

    @Test
    fun mermaidGraph_works() {
        val child = ShellStringBastNode()
        val root = PrintBastNode(child)
        val graph = root.mermaidGraph()
        assertFalse(graph.contains("reflection", true))
        assertFalse(graph.contains("BastNode"))
        log.info("Mermaid Graph: {}", graph)
    }

    @Test
    fun mermaidGraph_nodeNumbering_works() {
        val printNode = PrintBastNode(ShellStringBastNode("ls"), ShellStringBastNode("pwd"))
        val root = InternalBastNode(printNode)
        val graph = root.mermaidGraph()
        assertFalse(graph.contains("reflection", true))
        assertFalse(graph.contains("BastNode"))
        assertTrue(graph.contains("ShellString1"))
        log.info("Mermaid Graph: {}", graph)
    }

    /** Tests print(#(ls $(printf '.'; exit 1))) */
    @Test
    fun unnest_withPrint_works() {
        Main() // create for static state
        var printBastNode = PrintBastNode()
        var shellString = ShellStringBastNode()
        val ls = LeafBastNode("ls ")
        val subshell = ShellStringBastNode(listOf(LeafBastNode("echo '.'; exit 1")))
        shellString = shellString.replaceChildren(listOf(ls, subshell))
        printBastNode = printBastNode.replaceChildren(listOf(shellString))

        log.info("Mermaid Graph before unnest: {}", printBastNode.mermaidGraph())
        val unnestedRoot = printBastNode.unnestSubshells()
        log.info("Mermaid Graph after unnest: {}", unnestedRoot.mermaidGraph())
        assert(unnestedRoot.children.size == 2)
        assert(!unnestedRoot.children[0].render().endsWith(" "))
        assert(unnestedRoot.children[1].children.size == 1)
        assert(!unnestedRoot.children[1].render().startsWith(" "))
        assert(!unnestedRoot.children[1].render().endsWith(" "))

        val render = unnestedRoot.render()
        assertEquals("""
            declare __bp_var0
            __bp_var0="$(echo '.'; exit $SCRIPT_GENERIC_ERROR)"
            printf "$(ls ${'$'}{__bp_var0})"
            
        """.trimIndent(), render)
    }

    /** Tests set -euo pipefail; print(#(ls $(printf '.'; exit 1))) */
    @Test
    fun unnest_withPrintError_strictMode_exitsWithScriptError() {
        Main() // create for static state

        // create printNode
        var printBastNode = PrintBastNode()
        var shellString = ShellStringBastNode()
        val ls = LeafBastNode("ls ")
        val subshell = ShellStringBastNode(listOf(LeafBastNode("echo '.'; exit 1")))
        shellString = shellString.replaceChildren(listOf(ls, subshell))
        printBastNode = printBastNode.replaceChildren(listOf(shellString))

        // create parent of printNode and sibling
        val strictNode = ShellLineBastNode(listOf(LeafBastNode("set -euo pipefail")))
        val root = InternalBastNode(listOf(strictNode, printBastNode))

        log.info("Mermaid Graph before unnest: {}", root.mermaidGraph())
        val unnestedRoot = root.unnestSubshells()
        log.info("Mermaid Graph after unnest: {}", unnestedRoot.mermaidGraph())
        assert(unnestedRoot.children.size == 2)
        assert(unnestedRoot.children[1].children.size == 2)

        val render = unnestedRoot.render()
        assertEquals("""
            set -euo pipefail
            declare __bp_var0
            __bp_var0="$(echo '.'; exit $SCRIPT_GENERIC_ERROR)"
            printf "$(ls ${'$'}{__bp_var0})"
            
        """.trimIndent(), render)

        assertEquals(SCRIPT_GENERIC_ERROR, render.runCommand().second)
    }

    /** Tests set -euo pipefail; print(##(ls $(printf '.'; exit 1))) */
    @Test
    fun unnest_withPrintError_looseMode_runs() {
        Main() // create for static state

        // create printNode
        var printBastNode = PrintBastNode()
        var shellString = LooseShellStringBastNode()
        val ls = LeafBastNode("ls ")
        val subshell = ShellStringBastNode("echo '.'; exit 1")
        shellString = shellString.replaceChildren(listOf(ls, subshell))
        printBastNode = printBastNode.replaceChildren(listOf(shellString))

        // create parent of printNode and sibling
        val strictNode = ShellLineBastNode("""
            declare $OLD_OPTIONS
            $OLD_OPTIONS=$(set +o)
            set -euo pipefail
            
        """.trimIndent())
        val root = InternalBastNode(strictNode, printBastNode)

        log.info("Mermaid Graph before unnest: {}", root.mermaidGraph())
        val unnestedRoot = root.unnestSubshells()
        log.info("Mermaid Graph after unnest: {}", unnestedRoot.mermaidGraph())
        assert(unnestedRoot.children.size == 2)
        assert(unnestedRoot.children[1].children.size != 2)

        val looseNode = unnestedRoot.loosenShellStrings()
        log.info("Mermaid Graph after loosing: {}", looseNode.mermaidGraph())
        val render = looseNode.render()
        assertEquals(
            """
            declare $OLD_OPTIONS
            $OLD_OPTIONS=$(set +o)
            set -euo pipefail
            eval "${'$'}__bp_old_options"
            printf "$(ls $(echo '.'; exit $SCRIPT_GENERIC_ERROR))"
            set -euo pipefail
            
        """.trimIndent(), render)

        assertEquals(SCRIPT_SUCCESS, render.runCommand().second)
    }

}
