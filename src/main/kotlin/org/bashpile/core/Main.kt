package org.bashpile.core

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

fun main(args: Array<String>) = Main().main(args)

/** See `SystemTest` in `src/intTest/kotlin` for systems integration tests */
class Main : CliktCommand() {

    private val script by argument(help = "The script to compile")

    private val name by option("-n", "--name", help = "Your name")

    override fun run() {
        var propName = name
        if (script.isNotEmpty()) {
            val charStream = readSampleFile()
            val lexer = BashpileLexer(charStream)
            lexer.removeErrorListeners()
            lexer.addErrorListener(ThrowingErrorListener())
            val tokens = CommonTokenStream(lexer)
            val parser = BashpileParser(tokens)
            parser.removeErrorListeners()
            parser.addErrorListener(ThrowingErrorListener())
            val antlrAst = parser.program()
            val bast = BashpileVisitor().visitProgram(antlrAst)
            propName = bast.render()
        }
        echo("Hello ${propName ?: "World"}!", true)
    }

    private fun readSampleFile(): CharStream {
        val contextClassLoader = Thread.currentThread().contextClassLoader
        return contextClassLoader.getResourceAsStream("sample.properties").use { CharStreams.fromStream(it) }
    }
}
