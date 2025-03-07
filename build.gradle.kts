import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//////////////
// Main Config
//////////////

// gradle version specified at gradle/wrapper/gradle-wrapper.properties
val junitVersion = "5.12.0-M1"
val cliktVersion = "5.0.1"
val antlrVersion = "4.13.2"

plugins {
    antlr
    // kotlin version in plugins must be literal
    kotlin("jvm") version "2.1.10"
    application
}

group = "org.bashpile.core"
version = "0.3.0"

repositories {
    mavenCentral()
}

dependencies {

    // clikt
    implementation("com.github.ajalt.clikt:clikt:$cliktVersion")
    // optional support for rendering markdown in help messages
    implementation("com.github.ajalt.clikt:clikt-markdown:$cliktVersion")

    // antlr
    antlr("org.antlr:antlr4:$antlrVersion")
    implementation("com.yuvalshavit:antlr-denter:1.1")

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

////////////////////
// antlr integration
////////////////////

val generatedOutputFilename = "${layout.buildDirectory.get()}/generated/sources/main/java/antlr"

tasks.generateGrammarSource {
    // set output directory to some arbitrary location in `/build` directory.
    // by convention `/build/generated/sources/main/java/<generator name>` is often used
    outputDirectory = file(generatedOutputFilename)

    // pass -package to make generator put code in not default space
    arguments = listOf("-package", "org.bashpile.core", "-no-listener", "-visitor")
}

// workaround for antlr bug, should be fixed after 4.13.2
tasks.withType<KotlinCompile>().configureEach {
    dependsOn(tasks.withType<AntlrTask>())
}

sourceSets {
    main {
        java {
            srcDir(generatedOutputFilename)
        }
    }
}

////////////////////////////////
// Untar - for integration tests
////////////////////////////////

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

////////////////////////////////////////////////////////
// system tests - integration tests for the whole system
////////////////////////////////////////////////////////

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
