package org.bashpile.core

import kotlin.test.BeforeTest

open class MainTest {

    protected lateinit var fixture: Main

    @BeforeTest
    fun init() {
        fixture = Main()
    }
}