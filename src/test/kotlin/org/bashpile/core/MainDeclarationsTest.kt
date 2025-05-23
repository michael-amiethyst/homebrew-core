package org.bashpile.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.InputStream


/**
 * Tests Declarations, Assignments and Typing
 */
class MainDeclarationsTest {

    val fixture = Main()

    @Test
    fun getBast_declare_bool_works() {
        val bashpileText: InputStream = "b: bool = true".byteInputStream()
        val bashScript = fixture.getBast(bashpileText).render()
        assertEquals("""
            declare b
            b="true"

        """.trimIndent(), bashScript
        )

        val results = bashScript.runCommand()
        assertEquals(SCRIPT_SUCCESS, results.second)
    }

    @Test
    fun getBast_declare_readonlyExported_string_works() {
        val bashpileText: InputStream = "b: readonly exported string = 'A_STRING'".byteInputStream()
        val bashScript = fixture.getBast(bashpileText).render()
        assertEquals("""
            declare -x b
            b="A_STRING"

        """.trimIndent(), bashScript
        )

        val results = bashScript.runCommand()
        assertEquals(SCRIPT_SUCCESS, results.second)
    }

    @Test
    fun getBast_declare_readonlyExported_string_withPrintWorks() {
        val bashpileText: InputStream = """
            b: readonly exported string = 'A_STRING'
            print(b)
        """.trimIndent().byteInputStream()
        val bashScript = fixture.getBast(bashpileText).render()
        assertEquals("""
            declare -x b
            b="A_STRING"
            printf "${'$'}b"
        
        """.trimIndent(), bashScript
        )

        val results = bashScript.runCommand()
        assertEquals(SCRIPT_SUCCESS, results.second)
        assertEquals("A_STRING\n", results.first)
    }
}
