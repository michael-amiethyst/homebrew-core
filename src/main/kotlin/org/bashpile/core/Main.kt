package org.bashpile.core

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

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

    override fun run() {
        var bashTranslation = name
        if (script.isNotEmpty()) {
            val charStream = readFileAsAntlrStream("helloWorld.bps")
            val lexer = org.bashpile.core.BashpileLexer(charStream)
            lexer.removeErrorListeners()
            lexer.addErrorListener(ThrowingErrorListener())
            val tokens = CommonTokenStream(lexer)
            val parser = org.bashpile.core.BashpileParser(tokens)
            parser.removeErrorListeners()
            parser.addErrorListener(ThrowingErrorListener())
            val antlrAst = parser.program()
            val bast = BashpileVisitor().visitProgram(antlrAst)
            bashTranslation = bast.render()
        }
        echo(bashTranslation, true)
    }

    private fun readFileAsAntlrStream(filename: String): CharStream {
        val contextClassLoader = Thread.currentThread().contextClassLoader
        return contextClassLoader.getResourceAsStream(filename).use { CharStreams.fromStream(it) }
    }
}
