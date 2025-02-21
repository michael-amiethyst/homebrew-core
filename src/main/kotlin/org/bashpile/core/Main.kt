package org.bashpile.core

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.option

fun main(args: Array<String>) = Main().main(args)

class Main : CliktCommand() {

    private val name by option("-n", "--name", help = "Your name")

    override fun run() {
        echo("Hello ${name ?: "World"}!", true)
    }
}