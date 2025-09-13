package org.bashpile.core

import com.github.ajalt.clikt.testing.test
import org.bashpile.core.antlr.AstConvertingVisitor.Companion.STRICT_HEADER
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.InputStream
import java.lang.UnsupportedOperationException


/**
 * Tests Clikt and [Main._getBast], does not test logging.
 * See SystemTest for logger framework related tests (e.g. for `-vv` option).
 */
class MainTest {

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
        val output = fixture.test(arrayOf("src/test/resources/bpsScripts/hello.bps"))
        assertEquals(SCRIPT_SUCCESS, output.statusCode)
        assertTrue(output.stderr.isEmpty())
        assertEquals(STRICT_HEADER + """
            printf "Hello Bashpile!"
            
            """.trimIndent(), output.stdout)
    }

    @Test
    fun main_withConcatScript_works() {
        val output = fixture.test(arrayOf("src/test/resources/bpsScripts/helloWithConcat.bps"))
        assertEquals(SCRIPT_SUCCESS, output.statusCode)
        assertTrue(output.stderr.isEmpty())
        assertEquals(STRICT_HEADER + """
            printf "Hello Bashpile!"
            
            """.trimIndent(), output.stdout)
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
        val script: InputStream = "print(true)".byteInputStream()
        assertEquals(STRICT_HEADER + """
            printf "true"
            
            """.trimIndent(), fixture._getBast(script).render())
    }

    @Test
    fun getBast_printBool_withParens_works() {
        val bpScript: InputStream = "print(((true)))".byteInputStream()
        assertEquals(STRICT_HEADER + """
            printf "true"
            
            """.trimIndent(), fixture._getBast(bpScript).render())
    }

    @Test
    fun getBast_printString_tripleConcat_works() {
        val bpScript: InputStream = """
            print("Hello" + " " + "Bashpile!")""".trimIndent().byteInputStream()
        assertEquals(STRICT_HEADER + """
            printf "Hello Bashpile!"
            
            """.trimIndent(), fixture._getBast(bpScript).render())
    }

    @Test
    fun getBast_printString_tripleConcat_withParen_works() {
        val bpScript: InputStream = """
            print((("Hello" + " " + "Bashpile!")))""".trimIndent().byteInputStream()
        assertEquals(STRICT_HEADER + """
            printf "Hello Bashpile!"
            
            """.trimIndent(), fixture._getBast(bpScript).render())
    }

    @Test
    fun getBast_printString_tripleConcat_withMoreParens_works() {
        val bpScript: InputStream = """
            print(((("Hello" + " " + "Bashpile!" ) + "  It's " + "awesome!")))""".trimIndent().byteInputStream()
        assertEquals(STRICT_HEADER + """
            printf "Hello Bashpile!  It's awesome!"
            
            """.trimIndent(), fixture._getBast(bpScript).render())
    }

    @Test
    fun getBast_printFloat_works() {
        val bpScript: InputStream = "print(1.0)".byteInputStream()
        assertEquals(STRICT_HEADER + """
            printf "%s" "1.0"
            
            """.trimIndent(), fixture._getBast(bpScript).render())
    }

    @Test
    fun getBast_printFloat_plusString_fails() {
        val bpScript: InputStream = "print(1.0 + \" apples please\")".byteInputStream()
        assertThrows(UnsupportedOperationException::class.java) {
            fixture._getBast(bpScript).render()
        }
    }

    @Test
    fun getBast_printString_plusBool_fails() {
        val bpScript: InputStream = "print(\"You can't handle the \" + true)".byteInputStream()
        assertThrows(UnsupportedOperationException::class.java) {
            fixture._getBast(bpScript).render()
        }
    }
}
