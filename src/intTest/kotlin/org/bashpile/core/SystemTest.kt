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

    private fun String.runCommand(workingDir: File? = null): String {
        try {
            val cwd = System.getProperty("user.dir")
            val proc = ProcessBuilder(listOf("bash", "-c", ". ${'$'}HOME/.profile; $this"))
                .directory(workingDir ?: File(cwd))
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectErrorStream(true)
                .start()

            proc.waitFor(10, TimeUnit.SECONDS)

            // strip out blank lines and lines from sdkman, add newline back
            val text = proc.inputStream.bufferedReader().readText().split("\n")
            return text.stream()
                .filter { !it.contains("Using java version") }.collect(Collectors.joining()) + "\n"
        } catch(e: IOException) {
            return e.stackTraceToString()
        }
    }
}