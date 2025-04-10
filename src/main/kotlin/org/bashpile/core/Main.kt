package org.bashpile.core

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintHelpMessage
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.boolean
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory
import org.bashpile.core.bast.BashpileAst
import java.nio.file.Files
import java.nio.file.Path

// project TODOs here
// TODO generated /build/scripts should be "bashpile", not "bashpile-core"

fun main(args: Array<String>) = Main().main(args)

/**
 * Main entry point for the Bashpile compiler.
 * This is a simple command line interface (CLI) that takes a Bashpile script as input and outputs a Bash script.
 * This class is primarily responsible for parsing command line arguments.
 * See `SystemTest` in `src/intTest/kotlin` for systems integration tests.
 */
class Main : CliktCommand() {

    companion object {
        @JvmStatic
        val VERBOSE_ENABLED_MESSAGE = "Verbose logging enabled"
    }

    private val script by argument(help = "The script to compile")

    private val verboseLogging: Boolean by
    option("-v", "--verbose", help = "Enabable verbose (debug) logging").boolean().default(false)

    private val logger = LogManager.getLogger(Main::javaClass)

    /**
     * The main entry point for the Bashpile compiler.
     * This method is called by the `main` function by Clikt.
     * If we want to have an exitCode other than 0 on bad input we can override "parse" instead for manual control.
     */
    override fun run() {
        // guard, etc
        logger.info("Running Bashpile compiler with script: $script")
        val scriptPath = Path.of(script)
        if (Files.notExists(scriptPath) || !Files.isRegularFile(scriptPath)) {
            throw PrintHelpMessage(this.currentContext, true, SCRIPT_GENERIC_ERROR)
        }

        // configure log4j
        if (verboseLogging) {
            val configBuilder = ConfigurationBuilderFactory.newConfigurationBuilder()
            val configuration = configBuilder.add(configBuilder.newLogger("org.bashpile", Level.TRACE)).build(false)
            Configurator.reconfigure(configuration)
            logger.trace(VERBOSE_ENABLED_MESSAGE)
        }

        // get and render BAST tree
        val bast = runAntlrProcessing(scriptPath)
        echo(bast.render(), false)
    }

    private fun runAntlrProcessing(scriptPath: Path): BashpileAst {
        // setup lexer
        val stream = Files.newInputStream(scriptPath)
        val charStream = stream.use { CharStreams.fromStream(it) }
        val lexer = org.bashpile.core.BashpileLexer(charStream)
        lexer.removeErrorListeners()
        lexer.addErrorListener(ThrowingErrorListener())

        // setup parser
        val tokens = CommonTokenStream(lexer)
        val parser = org.bashpile.core.BashpileParser(tokens)
        parser.removeErrorListeners()
        parser.addErrorListener(ThrowingErrorListener())

        // handle ASTs and render
        val antlrAst = parser.program()
        val bast = AstConvertingVisitor().visitProgram(antlrAst)
        return bast
    }
}
