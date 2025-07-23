package org.bashpile.core

import com.github.ajalt.clikt.testing.test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.InputStream


/**
 * Tests Clikt and [Main.getBast], does not test logging.
 * See SystemTest for logger framework tests.
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
        assertEquals(
            """
            __bp_old_options=$(set +o)
            set -euo pipefail
            printf "Hello Bashpile!"
            
            """.trimIndent(), output.stdout)
    }

    @Test
    fun main_withConcatScript_works() {
        val output = fixture.test(arrayOf("src/test/resources/bpsScripts/helloWithConcat.bps"))
        assertEquals(SCRIPT_SUCCESS, output.statusCode)
        assertTrue(output.stderr.isEmpty())
        assertEquals(
            """
            __bp_old_options=$(set +o)
            set -euo pipefail
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
        assertEquals("""
            __bp_old_options=${'$'}(set +o)
            set -euo pipefail
            printf "true"
            
            """.trimIndent(), fixture.getBast(script).render())
    }

    @Test
    fun getBast_printBool_withParens_works() {
        val bpScript: InputStream = "print(((true)))".byteInputStream()
        assertEquals("""
            __bp_old_options=${'$'}(set +o)
            set -euo pipefail
            printf "true"
            
            """.trimIndent(), fixture.getBast(bpScript).render())
    }

    @Test
    fun getBast_printString_tripleConcat_works() {
        val bpScript: InputStream = """
            print("Hello" + " " + "Bashpile!")""".trimIndent().byteInputStream()
        assertEquals("""
            __bp_old_options=$(set +o)
            set -euo pipefail
            printf "Hello Bashpile!"
            
            """.trimIndent(), fixture.getBast(bpScript).render())
    }

    @Test
    fun getBast_printString_tripleConcat_withParen_works() {
        val bpScript: InputStream = """
            print((("Hello" + " " + "Bashpile!")))""".trimIndent().byteInputStream()
        assertEquals("""
            __bp_old_options=$(set +o)
            set -euo pipefail
            printf "Hello Bashpile!"
            
            """.trimIndent(), fixture.getBast(bpScript).render())
    }

    @Test
    fun getBast_printString_tripleConcat_withMoreParens_works() {
        val bpScript: InputStream = """
            print(((("Hello" + " " + "Bashpile!" ) + "  It's " + "awesome!")))""".trimIndent().byteInputStream()
        assertEquals("""
            __bp_old_options=$(set +o)
            set -euo pipefail
            printf "Hello Bashpile!  It's awesome!"
            
            """.trimIndent(), fixture.getBast(bpScript).render())
    }

    @Test
    fun getBast_printFloat_works() {
        val bpScript: InputStream = "print(1.0)".byteInputStream()
        assertEquals("""
            __bp_old_options=$(set +o)
            set -euo pipefail
            printf "1.0"
            
            """.trimIndent(), fixture.getBast(bpScript).render())
    }

    @Test
    fun getBast_printFloat_plusString_fails() {
        val bpScript: InputStream = "print(1.0 + \" apples please\")".byteInputStream()
        assertThrows(IllegalArgumentException::class.java) {
            fixture.getBast(bpScript).render()
        }
    }

    @Test
    fun getBast_printString_plusBool_fails() {
        val bpScript: InputStream = "print(\"You can't handle the \" + true)".byteInputStream()
        assertThrows(IllegalArgumentException::class.java) {
            fixture.getBast(bpScript).render()
        }
    }
}
