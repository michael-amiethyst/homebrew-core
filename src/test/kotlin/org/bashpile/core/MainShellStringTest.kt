package org.bashpile.core

import org.bashpile.core.antlr.AstConvertingVisitor.Companion.STRICT_HEADER
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import java.io.InputStream


/**
 * Tests Shell Strings and Shell Lines
 */
class MainShellStringTest {

    val fixture = Main()

    @Test
    fun getBast_shellLine_printf_works() {
        val script: InputStream = "printf \"true\"".byteInputStream()
        assertEquals(STRICT_HEADER + """
            printf "true"
            
            """.trimIndent(), fixture.getBast(script).render())
    }

    @Test
    fun getBast_shellLine_initialVar_works() {
        val script: InputStream = "test_var=5 printf \"\$test_var\"".byteInputStream()
        assertEquals(STRICT_HEADER + """
            test_var=5 printf "${'$'}test_var"
            
            """.trimIndent(), fixture.getBast(script).render())
    }

    @Test
    fun getBast_shellLine_literalNewline_works() {
        val script: InputStream = "printf \"newline\"".byteInputStream()
        assertEquals(STRICT_HEADER + """
            printf "newline"
            
            """.trimIndent(), fixture.getBast(script).render())
    }

    @Test
    fun getBast_shellstring_works() {
        val script: InputStream = "#(printf \"newline\")".byteInputStream()
        assertEquals(STRICT_HEADER + """
            $(printf "newline")
            
            """.trimIndent(), fixture.getBast(script).render())
    }

    @Test
    fun getBast_looseShellstring_works() {
        val script: InputStream = "##(printf \"newline\"; exit 1)".byteInputStream()
        assertEquals(STRICT_HEADER + """
            eval "${'$'}__bp_old_options"
            $(printf "newline"; exit 1)
            set -euo pipefail
            
            """.trimIndent(), fixture.getBast(script).render())
    }

    @Test
    fun getBast_shellstring_withConcat_works() {
        val script: InputStream = """
            print("Hello " + #(printf 'shellstring!'))""".trim().byteInputStream()
        assertEquals(STRICT_HEADER + """
            printf "Hello $(printf 'shellstring!')"
            """.trimIndent() + "\n", fixture.getBast(script).render())
    }

    @Test
    fun getBast_shellstring_nestedSubshells_works() {
        val script: InputStream = """
            print(#(ls $(echo '.')))""".trim().byteInputStream()
        val renderedBash = fixture.getBast(script).render()
        assertEquals(STRICT_HEADER + """
            declare __bp_var0
            __bp_var0="$(echo '.')"
            printf "$(ls ${'$'}{__bp_var0})"
            """.trimIndent() + "\n", renderedBash
        )
        assertTrue(renderedBash.runCommand().first.contains("bin"))
    }

    @Test
    fun getBast_shellstring_nestedSubshells_withInnerError_fails() {
        val pathname = "src/test/resources/bpsScripts/nestedSubshells.bps"
        val script: InputStream =  File(pathname).readText().trim().byteInputStream()
        val renderedBash = fixture.getBast(script).render()
        assertEquals(STRICT_HEADER + """
            set -euo pipefail
            declare __bp_var0
            __bp_var0="$(echo '.'; exit $SCRIPT_ERROR__GENERIC)"
            printf "$(ls ${'$'}{__bp_var0})"
            """.trimIndent() + "\n", renderedBash
        )
        val results = renderedBash.runCommand()
        assertEquals(SCRIPT_ERROR__GENERIC, results.second)
    }

    @Test
    fun getBast_shellstring_withShellStringConcat_works() {
        val script: InputStream = """
            print(#(printf "$(printf 'Hello ') $(printf 'shellstring!')"))""".trim().byteInputStream()
        val render = fixture.getBast(script).render()
        var renderedBash = render
        assertEquals(STRICT_HEADER + """
            declare __bp_var0
            __bp_var0="$(printf 'Hello ')"
            declare __bp_var1
            __bp_var1="$(printf 'shellstring!')"
            printf "$(printf "${'$'}{__bp_var0} ${'$'}{__bp_var1}")"
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
