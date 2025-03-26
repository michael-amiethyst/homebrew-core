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
        // TODO should be not equals
        assertEquals(SUCCESS, output.statusCode)
        assertTrue(output.stdout.startsWith("Usage:"))
    }

    @Test
    fun main_withoutScriptWithOption_printsHelp() {
        val output = Main().test(arrayOf("--name", "Jordi", ""))
        assertEquals(SUCCESS, output.statusCode)
        assertTrue(output.stdout.startsWith("Usage:"))
    }

    @Test
    fun main_withScript_works() {
        val output = Main().test(arrayOf(HELLO_FILENAME))
        assertEquals(SUCCESS, output.statusCode)
        assertEquals("Hello Bashpile!\n", output.stdout)
    }

    @Test
    fun main_withBadScript_fails() {
        val output = Main().test(arrayOf("non_existent_script.bps"))
        assertEquals(SUCCESS, output.statusCode)
        assertTrue(output.stdout.startsWith("Usage:"))
    }
}
