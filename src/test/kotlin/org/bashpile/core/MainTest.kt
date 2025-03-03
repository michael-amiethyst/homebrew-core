package org.bashpile.core

import com.github.ajalt.clikt.testing.test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.DefaultAsserter.fail

class MainTest {

    @Test
    fun mainWorks() {
        val output = Main().test(arrayOf("")).stdout
        assertEquals("Hello World!\n", output)
    }

    @Test
    fun mainWithOptionWorks() {
        val output = Main().test(arrayOf("--name", "Jordi", "")).stdout
        assertEquals("Hello Jordi!\n", output)
    }

    @Test
    fun mainWithArgumentWorks() {
        val output = Main().test(arrayOf("src/test/resources/bpsScripts/hello.bps"))
        if (output.statusCode != 0) {
            fail(output.stderr)
        }
        assertEquals("Hello Bashpile!\n", output.stdout)
    }
}