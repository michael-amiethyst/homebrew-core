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
        assertEquals(0, output.statusCode)
        assertTrue(output.stdout.startsWith("Usage:"))
    }

    @Test
    fun main_withoutScriptWithOption_printsHelp() {
        val output = Main().test(arrayOf("--name", "Jordi", ""))
        assertEquals(0, output.statusCode)
        assertTrue(output.stdout.startsWith("Usage:"))
    }

    @Test
    fun main_withScript_works() {
        val output = Main().test(arrayOf(HELLO_FILENAME))
        assertEquals(0, output.statusCode)
        assertEquals("Hello Bashpile!\n", output.stdout)
    }
}
