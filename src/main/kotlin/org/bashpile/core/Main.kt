package org.bashpile.core

import ch.qos.logback.classic.Level.DEBUG
import ch.qos.logback.classic.Level.INFO
import ch.qos.logback.classic.LoggerContext
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintHelpMessage
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.counted
import com.github.ajalt.clikt.parameters.options.option
import com.google.common.annotations.VisibleForTesting
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.apache.logging.log4j.LogManager
import org.bashpile.core.bast.BastNode
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path


fun main(args: Array<String>) = Main().main(args)

/**
 * Main entry point for the Bashpile compiler.
 * This is a simple command line interface (CLI) that takes a Bashpile script as the input and outputs a Bash script.
 * This class is primarily responsible for parsing command line arguments.
 * See `SystemTest` in `src/intTest/kotlin` for systems integration tests.
 */
class Main : CliktCommand() {

    companion object {
        const val VERBOSE_ENABLED_MESSAGE = "Double verbose (DEBUG) logging enabled"

        /** As in source/sink -> generates a startup message given a script filename */
        const val STARTUP_MESSAGE = "Running Bashpile compiler with script: "
    }

    private val script by argument(help = "The script to compile")

    private val verbosity by option("-v", "--verbose").counted(limit=2, clamp=true)

    private val logger = LogManager.getLogger(Main::javaClass)

    /**
     * The main entry point for the Bashpile compiler.
     * This method is called by the `main` function by Clikt.
     */
    override fun run() {
        // guard, etc
        val scriptPath = Path.of(script)
        if (Files.notExists(scriptPath) || !Files.isRegularFile(scriptPath)) {
            throw PrintHelpMessage(this.currentContext, true, SCRIPT_GENERIC_ERROR)
        }

        // configure logging
        if (verbosity > 0) {
            configureLogging(verbosity)
        }
        logger.info(STARTUP_MESSAGE + script) // first logging call after configureLogging() call

        // get and render BAST tree
        val script = Files.readString(scriptPath).stripShebang()
        val bastRoot: BastNode = getBast(script.byteInputStream())
        echo(bastRoot.render(), false)
    }

    /** The initial shebang line isn't part of the Bashpile script. */
    private fun String.stripShebang(): String {
        val shebang = "#!"
        return if (this.startsWith(shebang)) {
            this.stripFirstLine()
        } else {
            this
        }
    }

    /** Invokes ANTLR to parse the script and convert it to a Bashpile AST (BAST) */
    @VisibleForTesting
    internal fun getBast(stream: InputStream): BastNode {
        // setup lexer
        val charStream = stream.use { CharStreams.fromStream(it) }
        val lexer = BashpileLexer(charStream)
        lexer.removeErrorListeners()
        lexer.addErrorListener(ThrowingErrorListener())

        // setup parser
        val tokens = CommonTokenStream(lexer)
        val parser = BashpileParser(tokens)
        parser.removeErrorListeners()
        parser.addErrorListener(ThrowingErrorListener())

        // handle ASTs and render
        val antlrAst = parser.program()
        val bast = AstConvertingVisitor().visitProgram(antlrAst)
        return bast
    }

    /**
     * Defaults in src/main/resources/logback.xml
     *
     * We went with Logback because Log4j wasn't compatible with Graal nativeCompile.
     */
    private fun configureLogging(verbosity: Int) {
        val logbackLevel = when (verbosity) {
            1 -> INFO
            2 -> DEBUG
            else -> throw IllegalStateException("Unexpected verbosity level: $verbosity")
        }
        val context: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
        context.getLogger("org.bashpile").level = logbackLevel
        logger.debug(VERBOSE_ENABLED_MESSAGE)
    }
}
