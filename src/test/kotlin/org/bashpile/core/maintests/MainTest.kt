package org.bashpile.core.maintests

import org.bashpile.core.Main
import org.bashpile.core.antlr.AstConvertingVisitor.Companion.STRICT_HEADER
import org.bashpile.core.engine.RenderOptions.Companion.UNQUOTED
import org.bashpile.core.runCommand
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// TODO 0.19.0 - run shellcheck on them in a GHA
open class MainTest {

    companion object {
        const val DIRECTORY_PREFIX = "build/shellcheck"
        // framework makes a separate MainTest instance per test, so this needs to be here
        private var filenameCounter = 0
    }

    protected lateinit var fixture: Main


    @BeforeTest
    fun init() {
        fixture = Main()
        Files.createDirectories(Paths.get(DIRECTORY_PREFIX))
    }

    protected fun String.createRender(): String {
        val scriptStream = this.byteInputStream()
        return fixture._getBast(scriptStream).render(UNQUOTED)
    }

    protected fun assertRenderEquals(expectedBash: String, renderedBash: String) {
        assertTrue { renderedBash.startsWith(STRICT_HEADER) }
        assertEquals(STRICT_HEADER + expectedBash, renderedBash)

        val filename = Paths.get("$DIRECTORY_PREFIX/script${filenameCounter++}.bpc")
        Files.writeString(filename, renderedBash,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING)
    }

    protected fun String.assertRenderProduces(expectedStdout: String, expectedExitCode: Int = 0) {
        val results = runCommand()
        assertEquals(expectedStdout, results.first)
        assertEquals(expectedExitCode, results.second)
    }
}
