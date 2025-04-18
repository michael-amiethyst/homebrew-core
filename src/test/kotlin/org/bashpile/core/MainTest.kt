package org.bashpile.core

import com.github.ajalt.clikt.testing.test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class MainTest {
    companion object {
        const val HELLO_FILENAME = "src/test/resources/bpsScripts/hello.bps"
    }

    @Test
    fun main_withoutScript_printsHelp() {
        val output = Main().test(arrayOf(""))
        assertNotEquals(SCRIPT_SUCCESS, output.statusCode)
        assertTrue(output.stdout.startsWith("Usage:"))
    }

    @Test
    fun main_withoutScript_withOption_printsHelp() {
        val output = Main().test(arrayOf("--name", "Jordi", ""))
        assertNotEquals(SCRIPT_SUCCESS, output.statusCode)
        assertTrue(output.stderr.startsWith("Usage:"))
    }

    @Test
    fun main_withScript_works() {
        val output = Main().test(arrayOf(HELLO_FILENAME))
        assertEquals(SCRIPT_SUCCESS, output.statusCode)
        assertEquals("Hello Bashpile!\n", output.stdout)
    }

    @Test
    fun main_withScript_verbose_works() {
        val output = Main().test(arrayOf("--verbose=true", HELLO_FILENAME))
        assertEquals(SCRIPT_SUCCESS, output.statusCode)
        assertTrue(output.stdout.endsWith("Hello Bashpile!\n"), "Output: ${output.stdout}")
    }

    @Test
    fun main_withBadScript_fails() {
        val output = Main().test(arrayOf("non_existent_script.bps"))
        assertNotEquals(SCRIPT_SUCCESS, output.statusCode)
        assertTrue(output.stdout.startsWith("Usage:"))
    }
}
