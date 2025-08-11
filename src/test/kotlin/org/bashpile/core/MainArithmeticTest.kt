package org.bashpile.core

import org.bashpile.core.antlr.AstConvertingVisitor.Companion.STRICT_HEADER
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.InputStream


/**
 * Tests Clikt and [Main.getBast], does not test logging.
 * See SystemTest for logger framework tests.
 */
class MainArithmeticTest {

    val fixture = Main()

    @Test
    fun getBast_basicArithmatic_works() {
        val bpScript: InputStream = """
            print(1 + 1)""".trimIndent().byteInputStream()
        val render = fixture.getBast(bpScript).render()
        assertEquals(
            STRICT_HEADER + """
            printf "$((1 + 1))"
            
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
        val render = fixture.getBast(bpScript).render()
        assertEquals(
            STRICT_HEADER + """
            printf "$((1 - 1))"
            
            """.trimIndent(), render
        )
        val results = render.runCommand()
        assertEquals(SCRIPT_SUCCESS, results.second)
        assertEquals("0\n", results.first)
    }

    @Test
    fun getBast_complexArithmatic_works() {
        val bpScript: InputStream = """
            print(1 - (5 * 6))""".trimIndent().byteInputStream()
        val render = fixture.getBast(bpScript).render()
        assertEquals(
            STRICT_HEADER + """
            printf "$((1 - (5 * 6)))"
            
            """.trimIndent(), render
        )
        val results = render.runCommand()
        assertEquals(SCRIPT_SUCCESS, results.second)
        assertEquals("-29\n", results.first)
    }
}