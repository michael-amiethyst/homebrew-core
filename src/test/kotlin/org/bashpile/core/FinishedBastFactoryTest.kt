package org.bashpile.core

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bashpile.core.bast.BastNode
import org.bashpile.core.bast.InternalBastNode
import org.bashpile.core.bast.expressions.ShellStringBastNode
import org.bashpile.core.bast.statements.PrintBastNode
import org.bashpile.core.bast.statements.ShellLineBastNode
import org.bashpile.core.bast.types.leaf.LeafBastNode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class FinishedBastFactoryTest {

    val log: Logger = LogManager.getLogger()

    lateinit var fixture: FinishedBastFactory

    @BeforeEach
    fun setUp() {
        Main() // create for static state
        fixture = FinishedBastFactory()
        FinishedBastFactory.unnestedCount = 0
    }

    @Test
    fun mermaidGraph_works() {
        val child = ShellStringBastNode()
        val root = PrintBastNode(child)
        val graph = with(fixture) { root.mermaidGraph() }
        assertFalse(graph.contains("reflection", true))
        assertFalse(graph.contains("BastNode"))
        log.info("Mermaid Graph: {}", graph)
    }

    @Test
    fun mermaidGraph_nodeNumbering_works() {
        val printNode = PrintBastNode(ShellStringBastNode("ls"), ShellStringBastNode("pwd"))
        val root = InternalBastNode(printNode)
        val graph = with(fixture) { root.mermaidGraph() }
        assertFalse(graph.contains("reflection", true))
        assertFalse(graph.contains("BastNode"))
        assertTrue(graph.contains("ShellString1"))
        log.info("Mermaid Graph: {}", graph)
    }

    /** Tests print(#(ls $(printf '.'; exit 1))) */
    @Test
    fun unnest_withPrint_works() {
        var printBastNode = PrintBastNode()
        var shellString = ShellStringBastNode()
        val ls = LeafBastNode("ls ")
        val subshell = ShellStringBastNode(listOf(LeafBastNode("echo '.'; exit 1")))
        shellString = shellString.replaceChildren(listOf(ls, subshell))
        printBastNode = printBastNode.replaceChildren(listOf(shellString))
        val root = InternalBastNode(printBastNode)

        val unnestedRoot = with(fixture) {
            log.info("Mermaid Graph before unnest: {}", root.mermaidGraph())
            val unnested = fixture._unnestSubshells(root)
            log.info("Mermaid Graph after unnest: {}", unnested.mermaidGraph())
            unnested
        }

        val render = unnestedRoot.render()
        assertEquals("""
            declare __bp_var0
            __bp_var0="$(echo '.'; exit $SCRIPT_ERROR__GENERIC)"
            printf "$(ls ${'$'}{__bp_var0})"
            
        """.trimIndent(), render)
    }

    /** Tests set -euo pipefail; print(#(ls $(printf '.'; exit 1))) */
    @Test
    fun unnest_withPrintError_strictMode_exitsWithScriptError() {
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

        val unnestedRoot: BastNode = with(fixture) {
            log.info("Mermaid Graph before unnest: {}", root.mermaidGraph())
            val unnested: BastNode = fixture._unnestSubshells(root)
            log.info("Mermaid Graph after unnest: {}", unnested.mermaidGraph())
            unnested
        }

        val render = unnestedRoot.render()
        assertEquals("""
            set -euo pipefail
            declare __bp_var0
            __bp_var0="$(echo '.'; exit $SCRIPT_ERROR__GENERIC)"
            printf "$(ls ${'$'}{__bp_var0})"
            
        """.trimIndent(), render)

        assertEquals(SCRIPT_ERROR__GENERIC, render.runCommand().second)
    }
}