package org.bashpile.core

import org.bashpile.core.bast.BastNode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.io.InputStream


/**
 * Tests Shell Strings and Shell Lines
 */
class MainShellStringTest {

    val fixture = Main()

    @BeforeEach
    fun setUp() {
        BastNode.unnestedCount = 0
    }

    @Test
    fun getBast_shellLine_printf_works() {
        val script: InputStream = "printf \"true\"".byteInputStream()
        assertEquals("printf \"true\"\n", fixture.getBast(script).render().second)
    }

    @Test
    fun getBast_shellLine_initialVar_works() {
        val script: InputStream = "test_var=5 printf \"\$test_var\"".byteInputStream()
        assertEquals("test_var=5 printf \"\$test_var\"\n", fixture.getBast(script).render().second)
    }

    @Test
    fun getBast_shellLine_literalNewline_works() {
        val script: InputStream = "printf \"newline\"".byteInputStream()
        assertEquals("printf \"newline\"\n", fixture.getBast(script).render().second)
    }

    @Test
    fun getBast_shellstring_works() {
        val script: InputStream = "#(printf \"newline\")".byteInputStream()
        assertEquals("$(printf \"newline\")\n", fixture.getBast(script).render().second)
    }

    @Test
    fun getBast_shellstring_withConcat_works() {
        val script: InputStream = """
            print("Hello " + #(printf 'shellstring!'))""".trim().byteInputStream()
        assertEquals("""
            printf "Hello $(printf 'shellstring!')"
            """.trimIndent() + "\n", fixture.getBast(script).render().second)
    }

    @Test
    fun getBast_shellstring_nestedSubshells_works() {
        val script: InputStream = """
            print(#(printf $(printf 'shellstring!')))""".trim().byteInputStream()
        val renderedBash = fixture.getBast(script).render().second
        assertEquals("""
            declare __bp_var0
            __bp_var0="$(printf 'shellstring!')"
            printf "$(printf ${'$'}__bp_var0)"
            """.trimIndent() + "\n", renderedBash
        )
        assertEquals("shellstring!\n", renderedBash.runCommand().first)
    }

    @Test
    fun getBast_shellstring_nestedSubshells_withInnerError_fails() {
        val pathname = "src/test/resources/bpsScripts/nestedSubshells.bps"
        val script: InputStream =  File(pathname).readText().trim().byteInputStream()
        val renderedBash = fixture.getBast(script).render().second
        assertEquals("""
            set -euo pipefail
            declare __bp_var0
            __bp_var0="$(printf 'shellstring!'; exit $SCRIPT_GENERIC_ERROR)"
            printf "$(printf ${'$'}__bp_var0)"
            """.trimIndent() + "\n", renderedBash
        )
        val results = renderedBash.runCommand()
        assertEquals(SCRIPT_GENERIC_ERROR, results.second)
    }

    @Test
    fun getBast_shellstring_withShellStringConcat_works() {
        val script: InputStream = """
            print(#(printf "$(printf 'Hello ') $(printf 'shellstring!')"))""".trim().byteInputStream()
        val render = fixture.getBast(script).render()
        assertTrue(render.first.isEmpty())
        var renderedBash = render.second
        assertEquals("""
            declare __bp_var0
            __bp_var0="$(printf 'Hello ')"
            declare __bp_var1
            __bp_var1="$(printf 'shellstring!')"
            printf "$(printf "${'$'}__bp_var0 ${'$'}__bp_var1")"
            """.trimIndent() + "\n", renderedBash
        )
        var results = renderedBash.runCommand()
        assertEquals(SCRIPT_SUCCESS, results.second)

        // confirm succeeds with strict mode
        renderedBash = "set -euo pipefail\n" + renderedBash
        results = renderedBash.runCommand()
        assertEquals(SCRIPT_SUCCESS, results.second)
    }
}
