package org.bashpile.core.maintests

import kotlin.test.Test

class ArgumentsMainTest : MainTest() {
    override val testName: String = "ArgumentsMainTest"

    @Test
    fun argumentReferenceWorks() {
        val script = "print(arguments[1])".createRender()
        assertRenderEquals("printf \"$1\"\n", script)
            .assertRenderProduces("first\n", arguments = listOf("first"))
    }

    @Test
    fun argumentAllWorks() {
        // TODO change ##() syntax to #l() and create #v() for strings
        val script = """
            ##(IFS=\" \")
            print(arguments[all])
            
            """.trimIndent().createRender()
        assertRenderEquals("""
            IFS=" "
            printf "$@"
            
            """.trimIndent(), script
        ).assertRenderProduces("first second third\n", arguments = listOf("first", "second", "third"))
    }
}