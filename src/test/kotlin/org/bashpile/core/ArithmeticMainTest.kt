package org.bashpile.core

import org.bashpile.core.antlr.AstConvertingVisitor.Companion.STRICT_HEADER
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.InputStream


/**
 * Tests Clikt and [Main._getBast], does not test logging.
 * See SystemTest for logger framework tests.
 */
class ArithmeticMainTest {

    val fixture = Main()

    @Test
    fun getBast_basicArithmatic_works() {
        val bpScript: InputStream = """
            print(1 + 1)""".trimIndent().byteInputStream()
        val render = fixture._getBast(bpScript).render()
        assertEquals(
            STRICT_HEADER + """
            printf "%s" "$((1 + 1))"
            
            """.trimIndent(), render
        )
        val results = render.runCommand()
        assertEquals(SCRIPT_SUCCESS, results.second)
        assertEquals("2\n", results.first)
    }

    @Test
    fun getBast_basicArithmatic_subtraction_works() {
        val bpScript: InputStream = """
            print(1 - 1)""".trimIndent().byteInputStream()
        val render = fixture._getBast(bpScript).render()
        assertEquals(
            STRICT_HEADER + """
            printf "%s" "$((1 - 1))"
            
            """.trimIndent(), render
        )
        val results = render.runCommand()
        assertEquals(SCRIPT_SUCCESS, results.second)
        assertEquals("0\n", results.first)
    }

    @Test
    fun getBast_basicArithmatic_division_works() {
        val bpScript: InputStream = """
            print(6 / 4)""".trimIndent().byteInputStream()
        val render = fixture._getBast(bpScript).render()
        assertEquals(
            STRICT_HEADER + """
            printf "%s" "$((6 / 4))"
            
            """.trimIndent(), render
        )
        val results = render.runCommand()
        assertEquals(SCRIPT_SUCCESS, results.second)
        assertEquals("1\n", results.first)
    }

    @Test
    fun getBast_basicArithmatic_withTypecast_works() {
        val bpScript: InputStream = """
                one: string = "1"
                print(1 - one as integer)""".trimIndent().byteInputStream()
        val render = fixture._getBast(bpScript).render()
        assertEquals(
            STRICT_HEADER + """
                declare one
                one="1"
                printf "%s" "$((1 - ${'$'}{one}))"

            """.trimIndent(), render
        )
        val results = render.runCommand()
        assertEquals(SCRIPT_SUCCESS, results.second)
        assertEquals("0\n", results.first)
    }

    @Test
    fun getBast_basicArithmatic_withTypecast_andFloatAssignToInt_works() {
        val bpScript: InputStream = """
                four: string = "4"
                i: integer = 6 / four as integer
                print(i)""".trimIndent().byteInputStream()
        val render = fixture._getBast(bpScript).render()
        assertEquals(
            STRICT_HEADER + """
                declare four
                four="4"
                declare i
                i="$((6 / ${'$'}{four}))"
                printf "%s" "${'$'}{i}"

            """.trimIndent(), render
        )
        val results = render.runCommand()
        assertEquals(SCRIPT_SUCCESS, results.second)
        assertEquals("1\n", results.first)
    }

    /** We don't double-check the user */
    @Test
    fun getBast_basicArithmatic_withBadTypecast_works() {
        val bpScript: InputStream = """
                one: string = "one"
                print(1 - one as integer)""".trimIndent().byteInputStream()
        val render = fixture._getBast(bpScript).render()
        val results = render.runCommand()
        assertEquals(SCRIPT_ERROR__GENERIC, results.second)
    }

    @Test
    fun getBast_basicArithmatic_withTypecastAndParenthesis_works() {
        val bpScript: InputStream = """
                one: string = "1"
                print(1 - (one as integer))""".trimIndent().byteInputStream()
        val render = fixture._getBast(bpScript).render()
        assertEquals(
            STRICT_HEADER + """
                declare one
                one="1"
                printf "%s" "$((1 - (${'$'}{one})))"

            """.trimIndent(), render
        )
        val results = render.runCommand()
        assertEquals(SCRIPT_SUCCESS, results.second)
        assertEquals("0\n", results.first)
    }

    // TODO write tests for unary operators like ++ and --

    @Test
    fun getBast_complexArithmatic_works() {
        val bpScript: InputStream = """
            print(1 - (5 * 6))""".trimIndent().byteInputStream()
        val render = fixture._getBast(bpScript).render()
        assertEquals(
            STRICT_HEADER + """
            printf "%s" "$((1 - (5 * 6)))"
            
            """.trimIndent(), render
        )
        val results = render.runCommand()
        assertEquals(SCRIPT_SUCCESS, results.second)
        assertEquals("-29\n", results.first)
    }

    @Test
    fun getBast_basicFloatingPointArithmatic_works() {
        val bpScript: InputStream = """
            print(1.0 + .5)""".trimIndent().byteInputStream()
        val render = fixture._getBast(bpScript).render()
        assertEquals(
            STRICT_HEADER + """
            printf "%s" "$(bc <<< "1.0 + 0.5")"
            
            """.trimIndent(), render
        )
        val results = render.runCommand()
        assertEquals(SCRIPT_SUCCESS, results.second)
        assertEquals("1.5\n", results.first)
    }

    @Test
    fun getBast_floatingPointArithmatic_parenthesis_works() {
        val bpScript: InputStream = """
            print(1.0 - (30 * .5))""".trimIndent().byteInputStream()
        val render = fixture._getBast(bpScript).render()
        assertEquals(
            STRICT_HEADER + """
                printf "%s" "$(bc <<< "1.0 - (30 * 0.5)")"
            
            """.trimIndent(), render
        )
        val results = render.runCommand()
        assertEquals(SCRIPT_SUCCESS, results.second)
        assertEquals("-14.0\n", results.first)
    }

    @Test
    fun getBast_floatingPointArithmatic_noParenthesis_works() {
        val bpScript: InputStream = """
            print(1.0 - 30 * .5)""".trimIndent().byteInputStream()
        val render = fixture._getBast(bpScript).render()
        assertEquals(
            STRICT_HEADER + """
                printf "%s" "$(bc <<< "1.0 - 30 * 0.5")"
            
            """.trimIndent(), render
        )
        val results = render.runCommand()
        assertEquals(SCRIPT_SUCCESS, results.second)
        assertEquals("-14.0\n", results.first)
    }

    @Test
    fun getBast_floatingPointArithmatic_shellStringAndParenthesis_throws() {
        // ShellString is a... STRING
        val bpScript: InputStream = """
            print(#(expr 2 - 1) - (30 * .5))""".trimIndent().byteInputStream()
        assertThrows<UnsupportedOperationException> { fixture._getBast(bpScript).render() }
    }

    @Test
    fun getBast_floatingPointArithmatic_shellStringAndParenthesis_withTypecast_works() {
        val bpScript: InputStream = """
            print(#(expr 2 - 1) as integer - (30 * .5))""".trimIndent().byteInputStream()
        val render = fixture._getBast(bpScript).render()
        assertEquals(
            STRICT_HEADER + """
            declare __bp_var0
            __bp_var0="$(expr 2 - 1)"
            printf "%s" "$(bc <<< "${'$'}{__bp_var0} - (30 * 0.5)")"
            
            """.trimIndent(), render
        )
        val results = render.runCommand()
        assertEquals(SCRIPT_SUCCESS, results.second)
        assertEquals("-14.0\n", results.first)
    }
}