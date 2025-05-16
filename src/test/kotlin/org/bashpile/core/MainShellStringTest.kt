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
    fun getBast_shellString_printf_works() {
        val script: InputStream = "printf \"true\"".byteInputStream()
        assertEquals("printf \"true\"\n", fixture.getBast(script).render())
    }

    @Test
    fun getBast_shellString_initialVar_works() {
        val script: InputStream = "test_var=5 printf \"\$test_var\"".byteInputStream()
        assertEquals("test_var=5 printf \"\$test_var\"\n", fixture.getBast(script).render())
    }

    @Test
    fun getBast_shellString_literalNewline_works() {
        val script: InputStream = "printf \"newline\"".byteInputStream()
        assertEquals("printf \"newline\"\n", fixture.getBast(script).render())
    }
}
