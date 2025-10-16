package org.bashpile.core.maintests

import org.bashpile.core.Main
import kotlin.test.BeforeTest

// TODO 0.19.0 - have all tests save Bash to external files, run shellcheck on them in a GHA
open class MainTest {

    protected lateinit var fixture: Main

    @BeforeTest
    fun init() {
        fixture = Main()
    }
}
