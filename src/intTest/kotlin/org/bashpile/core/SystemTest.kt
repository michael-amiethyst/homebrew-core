package org.bashpile.core

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.PrintStream
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

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
        val output = "build/untar/bin/bashpile-core ''".runCommand()
        assertEquals("Hello World!\n", output)
    }

    @Test
    fun systemWithArgumentWorks() {
        val output = "build/untar/bin/bashpile-core --name Jordi ''".runCommand()
        assertEquals("Hello Jordi!\n", output)
    }
}