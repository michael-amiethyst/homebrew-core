package org.bashpile.core

import org.bashpile.core.antlr.AstConvertingVisitor.Companion.STRICT_HEADER
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.InputStream


/**
 * Tests Declarations, Assignments and Typing
 */
class DeclarationsMainTest : MainTest() {

    @Test
    fun getBast_declare_bool_works() {
        val bashpileText: InputStream = "b: boolean = true".byteInputStream()
        val bashScript = fixture._getBast(bashpileText).render()
        assertEquals(STRICT_HEADER + """
            declare b
            b="true"

        """.trimIndent(), bashScript)

        val results = bashScript.runCommand()
        assertEquals(SCRIPT_SUCCESS, results.second)
    }

    @Test
    fun getBast_declare_readonlyExported_string_works() {
        val bashpileText: InputStream = "b: readonly exported string = 'A_STRING'".byteInputStream()
        val bashScript = fixture._getBast(bashpileText).render()
        assertEquals(STRICT_HEADER + """
            declare -x b
            b="A_STRING"

        """.trimIndent(), bashScript)

        val results = bashScript.runCommand()
        assertEquals(SCRIPT_SUCCESS, results.second)
    }

    @Test
    fun getBast_declare_readonlyExported_string_withPrintWorks() {
        val bashpileText: InputStream = """
            b: readonly exported string = 'A_STRING'
            print(b)
        """.trimIndent().byteInputStream()
        val bashScript = fixture._getBast(bashpileText).render()
        assertEquals(STRICT_HEADER + """
            declare -x b
            b="A_STRING"
            printf "${'$'}{b}"
        
        """.trimIndent(), bashScript)

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
        val bashScript = fixture._getBast(bashpileText).render()
        assertEquals(STRICT_HEADER + """
            declare -x b
            b="A_STRING"
            b="B_STRING"
            printf "${'$'}{b}"
        
        """.trimIndent(), bashScript)

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
        val bashScript = fixture._getBast(bashpileText).render()
        assertEquals(STRICT_HEADER + """
            declare -x b
            b="A_STRING"
            b="'B_STRING'"
            printf "${'$'}{b}"

        """.trimIndent(), bashScript)

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
            fixture._getBast(bashpileText).render()
        }
    }

    @Test
    fun getBast_reassign_wrongType_fails() {
        val bashpileText: InputStream = """
            b: exported string = 'A_STRING'
            i: integer = 0
            b = i
            print(b)
        """.trimIndent().byteInputStream()
        assertThrows(IllegalStateException::class.java) {
            fixture._getBast(bashpileText).render()
        }
    }
}
