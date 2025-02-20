package org.bashpile.core

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class SystemTest {
    private lateinit var byteArrayOutputStream: ByteArrayOutputStream

    @BeforeEach
    fun setUp() {
        byteArrayOutputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(byteArrayOutputStream))
    }

    @AfterEach
    fun tearDown() {
        System.setOut(System.out)
    }

    @Test
    fun systemWorks() {
        Main.main(arrayOf())

        val output = byteArrayOutputStream.toString()
        assertEquals("Hello World!\n", output)
    }

    @Test
    fun systemWithArgumentWorks() {
        Main.main(arrayOf("Jordi"))

        val output = byteArrayOutputStream.toString()
        assertEquals("Hello Jordi!\n", output)
    }
}