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
    fun getBast_printBool_works() {
        val script: InputStream = "printf \"true\"".byteInputStream()
        assertEquals("printf \"true\"\n", fixture.getBast(script).render())
    }

    // TODO write tests for built-ins, functions, script executions (with initial variables too)

    // TODO write tests for shell strings with literal "newline" in them
}
