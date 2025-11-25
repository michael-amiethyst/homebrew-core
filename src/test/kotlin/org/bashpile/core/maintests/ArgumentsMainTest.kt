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

    // TODO arguments - create arguments[splat] and print with explicit format string
    @Test
    fun argumentAllWorks() {
        // ensure Bash is behaving as expected
        """
            printf "$@\n"

        """.trimIndent().assertRenderProduces(
            "first\n", arguments = listOf("first", "second", "third"))

        // ensure our Render is as expected
        val script = """
            print(arguments[all] + "\n")

            """.trimIndent().createRender()
        assertRenderEquals("""
            printf "$@\n"

            """.trimIndent(), script
        ).assertRenderProduces("first\n", arguments = listOf("first", "second", "third"))
    }
}