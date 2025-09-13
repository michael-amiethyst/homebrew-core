package org.bashpile.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class StringExtensionsKtTest {
    @Test
    fun runCommand_withMultiline_works() {
        val result = "echo 'Hello \n world'".runCommand()
        assertEquals("Hello \n world\n", result.first)
        assertEquals(SCRIPT_SUCCESS, result.second)
    }

    @Test
    fun runCommand_withDoubleQuotes_works() {
        val result = "echo \"Hello world\"".runCommand()
        assertEquals("Hello world\n", result.first)
        assertEquals(SCRIPT_SUCCESS, result.second)
    }

    /** Compiled 'print("Hello World")' @ Version 0.14.0 */
    @Test
    fun runCommand_withFullProgram_works() {
        val result = """
            declare -i s
            trap 's=$?; echo "Error (exit code ${'$'}s) found on line ${'$'}LINENO of generated Bash.\
              Command was: ${'$'}BASH_COMMAND"; exit ${'$'}s' ERR
            declare __bp_old_options
            __bp_old_options=$(set +o)
            set -euo pipefail
            printf "Hello World"
        """.trimIndent().runCommand()
        assertEquals("Hello World\n", result.first)
        assertEquals(SCRIPT_SUCCESS, result.second)
    }
}
