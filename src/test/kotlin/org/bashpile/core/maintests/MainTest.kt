package org.bashpile.core.maintests

import org.bashpile.core.Main
import kotlin.test.BeforeTest

open class MainTest {

    protected lateinit var fixture: Main

    @BeforeTest
    fun init() {
        fixture = Main()
    }
}
