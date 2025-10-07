package org.bashpile.core

import org.bashpile.core.antlr.AstConvertingVisitor.Companion.STRICT_HEADER
import kotlin.test.Test
import kotlin.test.assertEquals

class ConditionalMainTest {

    val fixture = Main()

    @Test
    fun ifStatement_works() {
        val renderedBash = fixture._getBast("""
            if (1 > 0):
                print("Math is mathing! ")
                print("Math is mathing!\n")
        """.trimIndent().byteInputStream()).render()
        assertEquals(STRICT_HEADER + """
            if [ 1 -gt 0 ]; then
                printf "Math is mathing! "
                printf "Math is mathing!\n"
            fi
            
        """.trimIndent(), renderedBash)
        val commandResult = renderedBash.runCommand()
        assertEquals("Math is mathing! Math is mathing!\n", commandResult.first)
        assertEquals(SCRIPT_SUCCESS, commandResult.second)
    }

    @Test
    fun ifElseStatement_works() {
        val renderedBash = fixture._getBast("""
            zero: integer = 0
            if (1 < zero):
                print("Math is not mathing\n")
            else:
                print("Math is mathing! ")
                print("Math is mathing!\n")
        """.trimIndent().byteInputStream()).render()
        assertEquals(STRICT_HEADER + """
            declare zero
            zero="0"
            if [ 1 -lt "${'$'}{zero}" ]; then
                printf "Math is not mathing\n"
            else
                printf "Math is mathing! "
                printf "Math is mathing!\n"
            fi
            
        """.trimIndent(), renderedBash)
        val commandResult = renderedBash.runCommand()
        assertEquals("Math is mathing! Math is mathing!\n", commandResult.first)
        assertEquals(SCRIPT_SUCCESS, commandResult.second)
    }

    // TODO fix if statement with shell string (should be no []'s in render)
    @Test
    fun ifElseStatement_withFailedShellString_works() {
        val renderedBash = fixture._getBast("""
            if (#(expr 1 > 0; exit $SCRIPT_ERROR__GENERIC)):
                print("Math is mathing! ")
                print("Math is mathing!\n")
            else:
                print("Command failed\n")
        """.trimIndent().byteInputStream()).render()
        assertEquals(STRICT_HEADER + """
            if [ $(expr 1 > 0; exit $SCRIPT_ERROR__GENERIC) ]; then
                printf "Math is mathing! "
                printf "Math is mathing!\n"
            else
                printf "Command failed\n"
            fi
            
        """.trimIndent(), renderedBash)
        val commandResult = renderedBash.runCommand()
        assertEquals("Command failed\n", commandResult.first)
        assertEquals(SCRIPT_SUCCESS, commandResult.second)
    }

    @Test
    fun ifElseIfElseStatement_works() {
        val renderedBash = fixture._getBast("""
            zero: integer = 0
            if (1 < zero):
                print("Math is not mathing\n")
            else if (zero < 1):
                print("Math is mathing! ")
                print("Math is mathing!\n")
            else:
                print("Zero is one???\n")
        """.trimIndent().byteInputStream()).render()
        assertEquals(STRICT_HEADER + """
            declare zero
            zero="0"
            if [ 1 -lt "${'$'}{zero}" ]; then
                printf "Math is not mathing\n"
            elif [ "${'$'}{zero}" -lt 1 ]; then
                printf "Math is mathing! "
                printf "Math is mathing!\n"
            else
                printf "Zero is one???\n"
            fi
            
        """.trimIndent(), renderedBash)
        val commandResult = renderedBash.runCommand()
        assertEquals("Math is mathing! Math is mathing!\n", commandResult.first)
        assertEquals(SCRIPT_SUCCESS, commandResult.second)
    }
}