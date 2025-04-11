package org.bashpile.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Overall System integration test for [Main].
 */
// TODO change to test the nativeCompile
class SystemTest {
    private val bashpileFilename = "build/untar/bin/bashpile"

    @Test
    fun system_withBadFilename_printsHelp() {
        val output = "$bashpileFilename BAD_FILENAME".runCommand()
        assertNotEquals(SCRIPT_SUCCESS, output.second)
        assertTrue(output.first.stripFirstLine().startsWith("Usage:"))
    }

    @Test
    fun systemWorks() {
        val output = "$bashpileFilename '${MainTest.HELLO_FILENAME}'".runCommand()
        assertEquals(SCRIPT_SUCCESS, output.second)
        assertEquals("Hello Bashpile!\n", output.first.stripFirstLine())
    }

    @Test
    fun system_withVerbose_works() {
        val output = "$bashpileFilename --verbose true '${MainTest.HELLO_FILENAME}'".runCommand()
        assertEquals(SCRIPT_SUCCESS, output.second)
        assertTrue(output.first.contains(Main.VERBOSE_ENABLED_MESSAGE))
        assertTrue(output.first.endsWith("Hello Bashpile!\n"))
    }

    /** Strip initial logging line */
    private fun String.stripFirstLine(): String = this.lines().drop(1).joinToString("\n")
}
