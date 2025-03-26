package org.bashpile.core

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

/** Shell script success (0), all other numbers are errors.  Generally 1-255. */
const val SUCCESS = 0

fun String.runCommand(workingDir: File? = null): Pair<String, Int> {
    var proc: Process? = null
    try {
        val cwd = System.getProperty("user.dir")
        proc = ProcessBuilder(listOf("bash", "-c", ". ${'$'}HOME/.profile; $this"))
            .directory(workingDir ?: File(cwd))
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectErrorStream(true)
            .start()

        proc.waitFor(10, TimeUnit.SECONDS)

        // strip out blank lines and lines from sdkman, add newline back
        val text = proc.inputStream.bufferedReader().readText().split("\n")
        val filteredText = text.filter { !it.contains("Using java version") }.joinToString("") + "\n"
        return Pair(filteredText, proc.exitValue())
    } catch(e: IOException) {
        return Pair(e.stackTraceToString(), proc?.exitValue() ?: -1)
    }
}
