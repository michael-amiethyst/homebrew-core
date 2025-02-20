package org.bashpile.core

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val name = if (args.isNotEmpty()) args[0] else "World"
            println("Hello $name!")
        }
    }
}