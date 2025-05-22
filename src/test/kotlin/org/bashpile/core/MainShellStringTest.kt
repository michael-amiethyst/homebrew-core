package org.bashpile.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.InputStream


/**
 * Tests Shell Strings and Shell Lines
 */
class MainShellStringTest {

    val fixture = Main()

    @Test
    fun getBast_shellLine_printf_works() {
        val script: InputStream = "printf \"true\"".byteInputStream()
        assertEquals("printf \"true\"\n", fixture.getBast(script).render())
    }

    @Test
    fun getBast_shellLine_initialVar_works() {
        val script: InputStream = "test_var=5 printf \"\$test_var\"".byteInputStream()
        assertEquals("test_var=5 printf \"\$test_var\"\n", fixture.getBast(script).render())
    }

    @Test
    fun getBast_shellLine_literalNewline_works() {
        val script: InputStream = "printf \"newline\"".byteInputStream()
        assertEquals("printf \"newline\"\n", fixture.getBast(script).render())
    }

    @Test
    fun getBast_shellstring_works() {
        val script: InputStream = "#(printf \"newline\")".byteInputStream()
        assertEquals("$(printf \"newline\")\n", fixture.getBast(script).render())
    }

    @Test
    fun getBast_shellstring_withConcat_works() {
        val script: InputStream = """
            print("Hello " + #(printf 'shellstring!'))""".trim().byteInputStream()
        assertEquals("""
            printf "Hello $(printf 'shellstring!')"
            """.trimIndent() + "\n", fixture.getBast(script).render())
    }

    @Test
    fun getBast_shellstring_nestedSubshells_works() {
        val script: InputStream = """
            print(#(printf $(printf 'shellstring!')))""".trim().byteInputStream()
        val renderedBash = fixture.getBast(script).render()
        assertEquals("""
            printf "$(printf $(printf 'shellstring!'))"
            """.trimIndent() + "\n", renderedBash
        )
        assertEquals("shellstring!\n", renderedBash.runCommand().first)
    }

    // TODO after assignments are implemented, uncomment and implement unwind logic
//    @Test
//    fun getBast_shellstring_nestedSubshells_withInnerError_fails() {
//        // TODO reference nestedSubshells.bps instead of literal string here
//        val script: InputStream = """
//            print(#(printf $(printf 'shellstring!'; exit $SCRIPT_GENERIC_ERROR)))""".trim().byteInputStream()
//        val renderedBash = fixture.getBast(script).render()
//        assertEquals("""
//            printf "$(printf $(printf 'shellstring!'; exit $SCRIPT_GENERIC_ERROR))"
//            """.trimIndent() + "\n", renderedBash
//        )
//        val results = renderedBash.runCommand()
//        assertEquals(SCRIPT_GENERIC_ERROR, results.second)
//    }

    @Test
    fun getBast_shellstring_withShellStringConcat_works() {
        val script: InputStream = """
            print(#(printf "$(printf 'Hello ') $(printf 'shellstring!')"))""".trim().byteInputStream()
        assertEquals("""
            printf "$(printf "$(printf 'Hello ') $(printf 'shellstring!')")"
            """.trimIndent() + "\n", fixture.getBast(script).render())
    }
}
