package org.bashpile.core

import org.bashpile.core.antlr.AstConvertingVisitor.Companion.STRICT_HEADER
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

    @Test
    fun runCommand_withForeachFileline_works() {
        val result = """
            declare -i s
            trap 's=$?; echo "Error (exit code ${'$'}s) found on line ${'$'}LINENO of generated Bash.\
              Command was: ${'$'}BASH_COMMAND"; exit ${'$'}s' ERR
            declare __bp_old_options
            __bp_old_options=$(set +o)
            set -euo pipefail
            cat src/test/resources/data/example.csv | while IFS=',' read -r FirstName LastName Email Phone; do
                printf "${'$'}FirstName ${'$'}LastName ${'$'}Email ${'$'}Phone\n"
            done
        """.trimIndent().runCommand()
        assertEquals("""
            FirstName LastName Email Phone
            Alice Smith alice.smith@email.com 555-1234
            Bob Johnson bob.j@email.com 555-5678
            Charlie Williams c.williams@email.com 555-9012
            
        """.trimIndent(), result.first)
        assertEquals(SCRIPT_SUCCESS, result.second)
    }

    @Test
    fun runCommand_withIf_works() {
        val result = """
            $STRICT_HEADER
            FILENAME=src/test/resources/data/example.csv
            if [[ -e ${'$'}FILENAME ]]; then
                printf "File Exists\n"
            fi
        """.trimIndent().runCommand()
        assertEquals("""
            File Exists
            
        """.trimIndent(), result.first)
        assertEquals(SCRIPT_SUCCESS, result.second)
    }
}
