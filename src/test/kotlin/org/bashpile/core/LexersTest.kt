package org.bashpile.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Path

@Order(3)
class LexersTest/* : BashpileTest() */{
    private val bashDir = "src/test/resources/bashScripts"

    @Test
    fun cdIsLinuxCommand() {
        assertTrue(Lexers.isLinuxCommand("cd ~"))
    }

    @Test
    fun awkIsLinuxCommand() {
        assertTrue(
            Lexers.isLinuxCommand(
                """
                awk 'BEGIN{RS="\1";ORS="";getline;gsub("\r","");print>ARGV[1]}' filename
                """.trimIndent()
            )
        )
    }

    @Test
    fun awkWithPreambleIsLinuxCommand() {
        assertTrue(
            Lexers.isLinuxCommand(
                """
                a=36 TEST='true' _test4="yes" awk 'BEGIN{RS="\1";ORS="";getline;gsub("\r","");print>ARGV[1]}' filename
                
                """.trimIndent()
            )
        )
    }

    @Test
    fun functionIsNotLinuxCommand() {
        assertFalse(Lexers.isLinuxCommand("function times2point5:float(x:float):"))
    }

    @Test
    @Throws(IOException::class)
    fun relativeCommandIsLinuxCommand() {
        val command = "$bashDir/my_ls.bash"
        // must be executable to register as a command
        assertEquals(0, "chmod +x $command".runCommand().second)
        assertTrue(Lexers.isLinuxCommand(command))
    }

    @Test
    fun relativeCommandWithArgumentIsLinuxCommand() {
        assertTrue(Lexers.isLinuxCommand("$bashDir/my_ls.bash escapedString.bps"))
    }

    @Test
    fun relativeCommandWithDotSlashIsLinuxCommand() {
        assertTrue(Lexers.isLinuxCommand("./$bashDir/my_ls.bash escapedString.bps"))
    }

    @Test
    fun absoluteCommandIsLinuxCommand() {
        val absolutePath = Path.of("./$bashDir/my_ls.bash").toAbsolutePath().toString()
        assertTrue(Lexers.isLinuxCommand(absolutePath), "$absolutePath was not a command")
    }

    @Test
    fun absoluteCommandWithDashesIsLinuxCommand() {
        val absolutePath = Path.of("./$bashDir/ls-with-dashes.bash-with-dashes")
            .toAbsolutePath().toString()
        assertTrue(Lexers.isLinuxCommand(absolutePath), "$absolutePath was not a command")
    }

    @Test
    fun elseIfIsNotLinuxCommand() {
        assertFalse(Lexers.isLinuxCommand("else-if check:"), "'else-if check:' was a command")
    }

    @Test
    fun printlnIsNotLinuxCommand() {
        assertFalse(Lexers.isLinuxCommand("println"), "'println' was a command")
    }
}