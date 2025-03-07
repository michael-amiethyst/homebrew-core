package org.bashpile.core

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors


fun String.runCommand(workingDir: File? = null): String {
    try {
        val cwd = System.getProperty("user.dir")
        val proc = ProcessBuilder(listOf("bash", "-c", ". ${'$'}HOME/.profile; $this"))
            .directory(workingDir ?: File(cwd))
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectErrorStream(true)
            .start()

        proc.waitFor(10, TimeUnit.SECONDS)

        // strip out blank lines and lines from sdkman, add newline back
        val text = proc.inputStream.bufferedReader().readText().split("\n")
        return text.stream()
            .filter { !it.contains("Using java version") }.collect(Collectors.joining()) + "\n"
    } catch(e: IOException) {
        return e.stackTraceToString()
    }
}