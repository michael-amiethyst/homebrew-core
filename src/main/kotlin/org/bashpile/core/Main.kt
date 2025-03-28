package org.bashpile.core

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintHelpMessage
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
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

    private val name by option("-n", "--name", help = "Your name")

    /**
     * The main entry point for the Bashpile compiler.
     * This method is called by the `main` function by Clikt.
     * If we want to have an exitCode other than 0 on bad input we can override "parse" instead for manual control.
     */
    // TODO add exit code
    override fun run() {
        // guard
        val scriptPath = Path.of(script)
        if (Files.notExists(scriptPath) || !Files.isRegularFile(scriptPath)) {
            throw PrintHelpMessage(this.currentContext)
        }

        // setup lexer
        val charStream = readFileAsAntlrStream(script)
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
    private fun readFileAsAntlrStream(filename: String): CharStream {
        val stream = Files.newInputStream(Path.of(filename))
        return stream.use { CharStreams.fromStream(it) }
    }
}
