package org.bashpile.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class MainKtTest {
    @Test
    fun mainWorks() {
        val byteArrayOutputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(byteArrayOutputStream))
        main()
        val output = byteArrayOutputStream.toString().trim()

        assertNotNull(output)
        assertEquals("Hello World!", output)
    }
}