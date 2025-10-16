package org.bashpile.core.maintests

import org.bashpile.core.SCRIPT_ERROR__GENERIC
import org.bashpile.core.SCRIPT_SUCCESS
import org.bashpile.core.antlr.AstConvertingVisitor.Companion.STRICT_HEADER
import org.bashpile.core.engine.RenderOptions.Companion.UNQUOTED
import org.bashpile.core.runCommand
import java.io.File
import java.io.InputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests Shell Strings and Shell Lines
 */
class ShellStringMainTest : MainTest() {

    @Test
    fun getBast_shellLine_printf_works() {
        val script: InputStream = "printf \"true\"".byteInputStream()
        assertEquals(STRICT_HEADER + """
            printf "true"
            
            """.trimIndent(), fixture._getBast(script).render(UNQUOTED)
        )
    }

    @Test
    fun getBast_shellLine_or_works() {
        val renderedBash = fixture._getBast("""
            ls some_random_file_that_does_not_exist.txt >/dev/null 2>&1 || true
            """.trimIndent().byteInputStream()).render(UNQUOTED)
        assertEquals(STRICT_HEADER + """
            ls some_random_file_that_does_not_exist.txt >/dev/null 2>&1 || true
            
            """.trimIndent(), renderedBash
        )
        val commandResult = renderedBash.runCommand()
        assertEquals("\n", commandResult.first)
        assertEquals(SCRIPT_SUCCESS, commandResult.second)
    }

    @Test
    fun getBast_shellLine_initialVar_works() {
        val script: InputStream = "test_var=5 printf \"\$test_var\"".byteInputStream()
        assertEquals(STRICT_HEADER + """
            test_var=5 printf "${'$'}test_var"
            
            """.trimIndent(), fixture._getBast(script).render(UNQUOTED)
        )
    }

    @Test
    fun getBast_shellLine_literalNewline_works() {
        val script: InputStream = "printf \"newline\"".byteInputStream()
        assertEquals(STRICT_HEADER + """
            printf "newline"
            
            """.trimIndent(), fixture._getBast(script).render(UNQUOTED)
        )
    }

    @Test
    fun getBast_shellstring_works() {
        val script: InputStream = "#(printf \"newline\")".byteInputStream()
        assertEquals(STRICT_HEADER + """
            $(printf "newline")
            
            """.trimIndent(), fixture._getBast(script).render(UNQUOTED)
        )
    }

    @Test
    fun getBast_looseShellstring_works() {
        val script: InputStream = "##(printf \"newline\"; exit 1)".byteInputStream()
        assertEquals(STRICT_HEADER + """
            eval "${'$'}__bp_old_options"
            $(printf "newline"; exit 1)
            set -euo pipefail
            
            """.trimIndent(), fixture._getBast(script).render(UNQUOTED)
        )
    }

    @Test
    fun getBast_shellstring_withConcat_works() {
        val script: InputStream = """
            print("Hello " + #(printf 'shellstring!'))""".trim().byteInputStream()
        assertEquals(STRICT_HEADER + """
            printf "Hello $(printf 'shellstring!')"
            """.trimIndent() + "\n", fixture._getBast(script).render(UNQUOTED)
        )
    }

    @Test
    fun getBast_shellstring_nestedSubshells_works() {
        val script: InputStream = """
            print(#(ls $(echo '.')))""".trim().byteInputStream()
        val renderedBash = fixture._getBast(script).render(UNQUOTED)
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
        val renderedBash = fixture._getBast(script).render(UNQUOTED)
        assertEquals(STRICT_HEADER + """
            set -euo pipefail
            declare __bp_var0
            __bp_var0="$(echo '.'; exit ${SCRIPT_ERROR__GENERIC})"
            printf "$(ls ${'$'}{__bp_var0})"
            """.trimIndent() + "\n", renderedBash
        )
        val results = renderedBash.runCommand()
        assertEquals(SCRIPT_ERROR__GENERIC, results.second)
        assertTrue(results.first.contains("Error (exit code ${SCRIPT_ERROR__GENERIC}) found"))
    }

    @Test
    fun getBast_shellstring_withShellStringConcat_works() {
        val script: InputStream = """
            print(#(printf "$(printf 'Hello ') $(printf 'shellstring!')"))""".trim().byteInputStream()
        val render = fixture._getBast(script).render(UNQUOTED)
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
        renderedBash = "set -euo pipefail\n$renderedBash"
        results = renderedBash.runCommand()
        assertEquals(SCRIPT_SUCCESS, results.second)
    }
}
