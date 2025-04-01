package org.bashpile.core

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintHelpMessage
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.apache.logging.log4j.LogManager
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

    private val script by argument(help = "The script to compile")

    // TODO implement -v --verbose option for Log4j logs, update log4j2.yaml to log to console
//    private val name by option("-n", "--name", help = "Your name")

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

        // setup lexer
        val charStream = readFileAsAntlrStream(scriptPath)
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
        echo(bast.render(), false)
    }

    /** Reads from FileSystem, use getResourceAsStream to read from classpath */
    private fun readFileAsAntlrStream(scriptPath: Path): CharStream {
        val stream = Files.newInputStream(scriptPath)
        return stream.use { CharStreams.fromStream(it) }
    }
}
