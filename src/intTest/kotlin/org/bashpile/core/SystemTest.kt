package org.bashpile.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE
import kotlin.io.path.exists
import kotlin.io.path.isExecutable

/**
 * Overall System integration test for [Main].
 */
class SystemTest {
    private val bashpileFilename = "build/native/nativeCompile/bashpile"
    private val shebangFilename = "build/resources/test/bpsScripts/shebang.bps"

    @Test
    fun system_withBadFilename_printsHelp() {
        val output = "$bashpileFilename BAD_FILENAME".runCommand()
        assertNotEquals(SCRIPT_SUCCESS, output.second)
        assertTrue(output.first.startsWith("Usage:"))
    }

    @Test
    fun systemWorks() {
        val output = "$bashpileFilename '${MainTest.HELLO_FILENAME}'".runCommand()
        assertEquals(SCRIPT_SUCCESS, output.second)
        assertEquals("printf \"Hello Bashpile!\\n\"\n", output.first)
    }

    @Test
    fun system_withVerbose_works() {
        val output = "$bashpileFilename --verbose '${MainTest.HELLO_FILENAME}'".runCommand()
        assertEquals(SCRIPT_SUCCESS, output.second)
        assertTrue(output.first.contains(Main.STARTUP_MESSAGE), "Output: ${output.first}")
        assertFalse(output.first.contains(Main.VERBOSE_ENABLED_MESSAGE), "Output: ${output.first}")
        assertTrue(output.first.endsWith("printf \"Hello Bashpile!\\n\"\n"))
    }

    @Test
    fun system_withDoubleVerbose_works() {
        val output = "$bashpileFilename -vv '${MainTest.HELLO_FILENAME}'".runCommand()
        assertEquals(SCRIPT_SUCCESS, output.second)
        assertTrue(output.first.contains(Main.STARTUP_MESSAGE), "Output: ${output.first}")
        assertTrue(output.first.contains(Main.VERBOSE_ENABLED_MESSAGE), "Output: ${output.first}")
        assertTrue(output.first.endsWith("printf \"Hello Bashpile!\\n\"\n"))
    }

    @Test
    fun system_shebang_works() {
        val shebangPath = Path.of(shebangFilename)
        assumeTrue(shebangPath.exists(), "Shebang test file not found")
        val path = shebangPath.makeExecutable()
        assumeTrue(path.isExecutable(), "Shebang test file not executable")
        val output = path.toString().runCommand()
        assertEquals(SCRIPT_SUCCESS, output.second, "Script not successful, output was: ${output.first}")
        assertEquals("printf \"Hello Shebang!\\n\"\n", output.first)
    }

    @Test
    fun system_withEval_works() {
        val output = "eval \"$($shebangFilename)\"".runCommand()
        assertEquals(SCRIPT_SUCCESS, output.second)
        assertEquals("Hello Shebang!\n", output.first)
    }

    private fun Path.makeExecutable(): Path {
        if (!this.isExecutable()) {
            val perms = Files.getPosixFilePermissions(this)
            perms.add(OWNER_EXECUTE)
            Files.setPosixFilePermissions(this, perms)
        }
        return this
    }
}
