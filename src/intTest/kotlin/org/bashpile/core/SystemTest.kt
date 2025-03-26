package org.bashpile.core

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**
 * Overall System integration test for [Main].
 */
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
        val output = "build/untar/bin/bashpile-core '${MainTest.HELLO_FILENAME}'".runCommand()
        assertEquals("Hello Bashpile!\n", output.first)
        assertEquals(0, output.second)
    }

    @Test
    fun systemWithArgumentWorks() {
        val output = "build/untar/bin/bashpile-core --name Jordi '${MainTest.HELLO_FILENAME}'".runCommand()
        assertEquals("Hello Bashpile!\n", output.first)
        assertEquals(0, output.second)
    }
}
