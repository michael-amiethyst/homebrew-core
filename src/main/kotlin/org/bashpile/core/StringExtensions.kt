package org.bashpile.core

import org.apache.commons.lang3.Strings
import java.io.File
import java.io.IOException
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/** Shell script success (0), all other numbers are errors.  Generally 1-255. */
const val SCRIPT_SUCCESS = 0
const val SCRIPT_ERROR__GENERIC = 1

private val executors = Executors.newFixedThreadPool(8)

private var profileLocation = "${'$'}HOME/.profile"

/** Strip initial logging line */
fun String.stripFirstLine(): String = this.lines().drop(1).joinToString("\n")

fun String.appendIfMissing(suffix: String): String = Strings.CS.appendIfMissing(this, suffix)

fun String.runCommand(workingDir: File? = null): Pair<String, Int> {
    var proc: Process? = null
    try {
        val callable: Callable<Process> = Callable {
            val cwd = System.getProperty("user.dir")
            val proc2 = ProcessBuilder(listOf("bash", "-c", ". $profileLocation; $this"))
                .directory(workingDir ?: File(cwd))
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectErrorStream(true)
                .start()

            if (!proc2.waitFor(10, TimeUnit.SECONDS)) {
                proc2.destroyForcibly()
            }
            return@Callable proc2
        }

        proc = executors.submit(callable).get(10, TimeUnit.SECONDS)

        // strip out blank lines and lines from sdkman, add newline back
        val text = proc.inputStream.bufferedReader().readText().trim()
        val lines = text.split("\n")
        val filteredText = lines
            .filter { !it.contains("Using java version") }
            .joinToString("\n") + "\n"

        // check for bad profileLocation and cache good location
        val noFile = "No such file or directory"
        return if (!filteredText.contains(noFile)) {
            Pair(filteredText, proc.exitValue())
        } else if (filteredText.contains(".profile: $noFile")) {
            profileLocation = "${'$'}HOME/.bash_profile"
            runCommand(workingDir)
        } else if (filteredText.contains(".bash_profile: $noFile")) {
            profileLocation = "${'$'}HOME/.bashrc"
            runCommand(workingDir)
        } else {
            throw IllegalStateException(
                "Could not find valid profile.  Checked ~/.profile, ~/.bash_profile, ~/.bashrc.  \n" +
                        "Command results: $filteredText")
        }
    } catch(e: IOException) {
        return Pair(e.stackTraceToString(), proc?.exitValue() ?: -1)
    }
}
