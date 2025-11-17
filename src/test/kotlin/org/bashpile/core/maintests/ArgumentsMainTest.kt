package org.bashpile.core.maintests

import kotlin.test.Test

class ArgumentsMainTest : MainTest() {
    override val testName: String = "ArgumentsMainTest"

    @Test
    fun argumentReferenceWorks() {
        val script = "print(arguments[1])".createRender()
        assertRenderEquals("printf \"$1\"", script)
            .assertRenderProduces("first", arguments = listOf("first"))
    }
}