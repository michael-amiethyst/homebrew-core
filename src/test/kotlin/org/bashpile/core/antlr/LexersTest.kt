package org.bashpile.core.antlr

import org.bashpile.core.SCRIPT_SUCCESS
import org.bashpile.core.runCommand
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Path

@Order(3)
class LexersTest {
    private val bashDir = "src/test/resources/bashScripts"

    @Test
    fun cdIsLinuxCommand() {
        Assertions.assertTrue(Lexers._isLinuxCommand("cd ~"))
    }

    @Test
    fun awkIsLinuxCommand() {
        Assertions.assertTrue(
            Lexers._isLinuxCommand(
                """
                awk 'BEGIN{RS="\1";ORS="";getline;gsub("\r","");print>ARGV[1]}' filename
                """.trimIndent()
            )
        )
    }

    @Test
    fun awkWithPreambleIsLinuxCommand() {
        Assertions.assertTrue(
            Lexers._isLinuxCommand(
                """
                a=36 TEST='true' _test4="yes" awk 'BEGIN{RS="\1";ORS="";getline;gsub("\r","");print>ARGV[1]}' filename
                
                """.trimIndent()
            )
        )
    }

    @Test
    fun functionIsNotLinuxCommand() {
        Assertions.assertFalse(Lexers._isLinuxCommand("function times2point5:float(x:float):"))
    }

    @Test
    @Throws(IOException::class)
    fun relativeCommandIsLinuxCommand() {
        val command = "$bashDir/my_ls.bash"
        // must be executable to register as a command
        Assertions.assertEquals(SCRIPT_SUCCESS, "chmod +x $command".runCommand().second)
        Assertions.assertTrue(Lexers._isLinuxCommand(command))
    }

    @Test
    fun relativeCommandWithArgumentIsLinuxCommand() {
        Assertions.assertTrue(Lexers._isLinuxCommand("$bashDir/my_ls.bash escapedString.bps"))
    }

    @Test
    fun relativeCommandWithDotSlashIsLinuxCommand() {
        Assertions.assertTrue(Lexers._isLinuxCommand("./$bashDir/my_ls.bash escapedString.bps"))
    }

    @Test
    fun absoluteCommandIsLinuxCommand() {
        val absolutePath = Path.of("./$bashDir/my_ls.bash").toAbsolutePath().toString()
        Assertions.assertTrue(Lexers._isLinuxCommand(absolutePath), "$absolutePath was not a command")
    }

    @Test
    fun absoluteCommandWithDashesIsLinuxCommand() {
        val absolutePath = Path.of("./$bashDir/ls-with-dashes.bash-with-dashes")
            .toAbsolutePath().toString()
        Assertions.assertTrue(Lexers._isLinuxCommand(absolutePath), "$absolutePath was not a command")
    }

    @Test
    fun elseIfIsNotLinuxCommand() {
        Assertions.assertFalse(Lexers._isLinuxCommand("else-if check:"), "'else-if check:' was a command")
    }

    @Test
    fun printlnIsNotLinuxCommand() {
        Assertions.assertFalse(Lexers._isLinuxCommand("println"), "'println' was a command")
    }
}