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

    @Test
    fun system_withBadFilename_printsHelp() {
        val output = "$bashpileFilename BAD_FILENAME".runCommand()
        assertNotEquals(SCRIPT_SUCCESS, output.second)
        assertTrue(output.first.stripFirstLine().startsWith("Usage:"))
    }

    @Test
    fun systemWorks() {
        val output = "$bashpileFilename '${MainTest.HELLO_FILENAME}'".runCommand()
        assertEquals(SCRIPT_SUCCESS, output.second)
        assertEquals("printf \"Hello Bashpile!\\n\"\n", output.first.stripFirstLine())
    }

    @Test
    fun system_withVerbose_works() {
        val output = "$bashpileFilename --verbose true '${MainTest.HELLO_FILENAME}'".runCommand()
        assertEquals(SCRIPT_SUCCESS, output.second)
        assertTrue(output.first.contains(Main.VERBOSE_ENABLED_MESSAGE))
        assertTrue(output.first.endsWith("printf \"Hello Bashpile!\\n\"\n"))
    }

    @Test
    fun system_shebang_works() {
        val shebangPath = Path.of("build/resources/test/bpsScripts/shebang.bps")
        assumeTrue(shebangPath.exists(), "Shebang test file not found")
        val path = shebangPath.makeExecutable()
        assumeTrue(path.isExecutable(), "Shebang test file not executable")
        val output = path.toString().runCommand()
        assertEquals(SCRIPT_SUCCESS, output.second, "Script not successful, output was: ${output.first}")
        assertEquals("printf \"Hello Shebang!\\n\"\n", output.first.stripFirstLine())
    }

    // TODO write an immediately executing script

    private fun Path.makeExecutable(): Path {
        if (!this.isExecutable()) {
            val perms = Files.getPosixFilePermissions(this)
            perms.add(OWNER_EXECUTE)
            Files.setPosixFilePermissions(this, perms)
        }
        return this
    }
}
