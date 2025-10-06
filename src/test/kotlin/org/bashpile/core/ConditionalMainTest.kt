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
                print("Math is mathing\n")
        """.trimIndent().byteInputStream()).render()
        assertEquals(STRICT_HEADER + """
            if [ 1 -gt 0 ]; then
                printf "Math is mathing\n"
            fi
            
        """.trimIndent(), renderedBash)
    }
}