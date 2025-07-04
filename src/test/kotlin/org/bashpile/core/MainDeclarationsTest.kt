package org.bashpile.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.InputStream


/**
 * Tests Declarations, Assignments and Typing
 */
class MainDeclarationsTest {

    var fixture = Main()

    @BeforeEach
    fun setUp() {
        fixture = Main()
    }

    @Test
    fun getBast_declare_bool_works() {
        val bashpileText: InputStream = "b: boolean = true".byteInputStream()
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

    @Test
    fun getBast_reassign_works() {
        val bashpileText: InputStream = """
            b: exported string = 'A_STRING'
            b="B_STRING"
            print(b)
        """.trimIndent().byteInputStream()
        val bashScript = fixture.getBast(bashpileText).render()
        assertEquals("""
            declare -x b
            b="A_STRING"
            b="B_STRING"
            printf "${'$'}b"
        
        """.trimIndent(), bashScript
        )

        val results = bashScript.runCommand()
        assertEquals(SCRIPT_SUCCESS, results.second)
        assertEquals("B_STRING\n", results.first)
    }

    @Test
    fun getBast_reassign_withLiteralQuotes_works() {
        val bashpileText: InputStream = """
            b: exported string = 'A_STRING'
            b="'B_STRING'"
            print(b)
        """.trimIndent().byteInputStream()
        val bashScript = fixture.getBast(bashpileText).render()
        assertEquals("""
            declare -x b
            b="A_STRING"
            b="'B_STRING'"
            printf "${'$'}b"
        
        """.trimIndent(), bashScript
        )

        val results = bashScript.runCommand()
        assertEquals(SCRIPT_SUCCESS, results.second)
        assertEquals("'B_STRING'\n", results.first)
    }

    @Test
    fun getBast_reassign_readonly_fails() {
        val bashpileText: InputStream = """
            b: readonly exported string = 'A_STRING'
            b = "B_STRING"
            print(b)
        """.trimIndent().byteInputStream()
        assertThrows(IllegalStateException::class.java) {
            fixture.getBast(bashpileText).render()
        }
    }

    @Test
    fun getBast_reassign_wrongType_fails() {
        val bashpileText: InputStream = """
            b: exported string = 'A_STRING'
            i: int = 0
            b = i
            print(b)
        """.trimIndent().byteInputStream()
        assertThrows(IllegalStateException::class.java) {
            fixture.getBast(bashpileText).render()
        }
    }
}
