plugins {
    kotlin("jvm") version "1.9.23"
    application
}

group = "org.bashpile.core"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "org.bashpile.core.MainKt"
}