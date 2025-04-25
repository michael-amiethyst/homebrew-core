package org.bashpile.core

import com.github.ajalt.clikt.testing.test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.InputStream


class MainTest {
    companion object {
        const val HELLO_FILENAME = "src/test/resources/bpsScripts/hello.bps"
        const val HELLO_CONCAT_FILENAME = "src/test/resources/bpsScripts/helloWithConcat.bps"
    }

    val fixture = Main()

    @Test
    fun main_withoutScript_printsHelp() {
        val output = fixture.test(arrayOf(""))
        assertNotEquals(SCRIPT_SUCCESS, output.statusCode)
        assertTrue(output.stdout.startsWith("Usage:"))
    }

    @Test
    fun main_withoutScript_withOption_printsHelp() {
        val output = fixture.test(arrayOf("--name", "Jordi", ""))
        assertNotEquals(SCRIPT_SUCCESS, output.statusCode)
        assertTrue(output.stderr.startsWith("Usage:"))
    }

    @Test
    fun main_withScript_works() {
        val output = fixture.test(arrayOf(HELLO_FILENAME))
        assertEquals(SCRIPT_SUCCESS, output.statusCode)
        assertEquals("printf \"Hello Bashpile!\\n\"", output.stdout)
        assertTrue(output.stderr.isEmpty())
    }

    @Test
    fun main_withConcatScript_works() {
        val output = fixture.test(arrayOf(HELLO_CONCAT_FILENAME))
        assertEquals(SCRIPT_SUCCESS, output.statusCode)
        assertEquals("printf \"Hello Bashpile!\\n\"", output.stdout)
    }

    @Test
    fun main_withScript_verbose_works() {
        val output = fixture.test(arrayOf("--verbose=true", HELLO_FILENAME))
        assertEquals(SCRIPT_SUCCESS, output.statusCode)
        assertTrue(output.stdout.endsWith("printf \"Hello Bashpile!\\n\""), "Output: ${output.stdout}")
    }

    @Test
    fun main_withBadScript_fails() {
        val output = fixture.test(arrayOf("non_existent_script.bps"))
        assertNotEquals(SCRIPT_SUCCESS, output.statusCode)
        assertTrue(output.stdout.startsWith("Usage:"))
    }

    // getBast tests

    @Test
    fun getBast_printBool_works() {
        val printBool: InputStream = "print(true)".byteInputStream()
        assertEquals("printf \"true\\n\"", fixture.getBast(printBool).render())
    }

    @Test
    fun getBast_printString_tripleConcat_works() {
        val printBool: InputStream = """
            print("Hello" + " " + "Bashpile!")""".trimIndent().byteInputStream()
        assertEquals("printf \"Hello Bashpile!\\n\"", fixture.getBast(printBool).render())
    }

    @Test
    fun getBast_printFloat_works() {
        val printBool: InputStream = "print(1.0)".byteInputStream()
        assertEquals("printf \"1.0\\n\"", fixture.getBast(printBool).render())
    }

    @Test
    fun getBast_printFloat_plusString_fails() {
        val printBool: InputStream = "print(1.0 + \" apples please\")".byteInputStream()
        assertThrows(IllegalArgumentException::class.java) {
            fixture.getBast(printBool).render()
        }
    }

    @Test
    fun getBast_printString_plusBool_fails() {
        val printStringAndBool: InputStream = "print(\"You can't handle the \" + true)".byteInputStream()
        assertThrows(IllegalArgumentException::class.java) {
            fixture.getBast(printStringAndBool).render()
        }
    }
}
