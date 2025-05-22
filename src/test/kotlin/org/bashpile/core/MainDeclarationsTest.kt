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

    // TODO write declare and print test
}
