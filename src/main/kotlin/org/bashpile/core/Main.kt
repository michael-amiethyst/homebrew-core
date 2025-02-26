package org.bashpile.core

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option

fun main(args: Array<String>) = Main().main(args)

/** See `SystemTest` in `src/intTest/kotlin` for systems integration tests */
class Main : CliktCommand() {

    private val script by argument(help = "The script to compile")

    private val name by option("-n", "--name", help = "Your name")

    override fun run() {
        echo("Hello ${name ?: "World"}!", true)
    }
}