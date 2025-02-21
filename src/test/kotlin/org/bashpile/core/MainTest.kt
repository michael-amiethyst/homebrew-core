package org.bashpile.core

import com.github.ajalt.clikt.testing.test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MainTest {

    @Test
    fun mainWorks() {
        val output = Main().test(arrayOf()).stdout
        assertEquals("Hello World!\n", output)
    }

    @Test
    fun mainWithArgumentWorks() {
        val output = Main().test(arrayOf("--name", "Jordi")).stdout
        assertEquals("Hello Jordi!\n", output)
    }
}