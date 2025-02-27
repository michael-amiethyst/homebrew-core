//////////////
// Main Config
//////////////

// gradle version specified at gradle/wrapper/gradle-wrapper.properties
val junitVersion = "5.12.0-M1"
val cliktVersion = "5.0.1"

plugins {
    // kotlin version in plugins must be literal
    kotlin("jvm") version "2.1.10"
    application
}

group = "org.bashpile.core"
version = "0.2.0"

repositories {
    mavenCentral()
}

dependencies {

    // clikt
    implementation("com.github.ajalt.clikt:clikt:$cliktVersion")
    // optional support for rendering markdown in help messages
    implementation("com.github.ajalt.clikt:clikt-markdown:$cliktVersion")

    // tests
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")

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

////////
// Untar - for integration tests
////////

tasks.register<Exec>("untar") {
    group = "verification"
    workingDir = File("build/distributions")
    commandLine = listOf("tar", "-xf", "bashpile-core-$version.tar")
    shouldRunAfter("test", "assemble")
    dependsOn("test", "assemble")
}

// create build/untar directory
tasks.register<Exec>("mv") {
    group = "verification"
    workingDir = File("build")
    commandLine = listOf("mv", "-f", "distributions/bashpile-core-$version", "untar")
    shouldRunAfter("untar")
    dependsOn("untar")
}

///////////////
// system tests - integration tests for the whole system
///////////////

sourceSets {
    create("intTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val intTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}
val intTestRuntimeOnly: Configuration by configurations.getting

configurations["intTestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

dependencies {
    intTestImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    intTestRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val integrationTest = task<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["intTest"].output.classesDirs
    classpath = sourceSets["intTest"].runtimeClasspath
    shouldRunAfter("mv")
    dependsOn("mv")

    useJUnitPlatform()

    testLogging {
        events("passed")
    }
}

tasks.check { dependsOn(integrationTest) }