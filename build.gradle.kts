import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//////////////
// Main Config
//////////////

// gradle version specified at gradle/wrapper/gradle-wrapper.properties
val antlrVersion = "4.13.2"
val cliktVersion = "5.0.1"
val junitVersion = "5.12.0-M1"

plugins {
    antlr
    // kotlin version in plugins must be literal
    kotlin("jvm") version "2.1.10"
    id("org.graalvm.buildtools.native") version "0.10.6"
    id("org.gradlex.jvm-dependency-conflict-detection") version "2.2"
    id("com.adarshr.test-logger") version "4.0.0"
}

group = "org.bashpile.core"
version = "0.7.0"

repositories {
    mavenCentral()
}

dependencies {

    // clikt
    implementation("com.github.ajalt.clikt:clikt:$cliktVersion")
    // optional support for rendering Markdown in help messages
    implementation("com.github.ajalt.clikt:clikt-markdown:$cliktVersion")

    // antlr
    antlr("org.antlr:antlr4:$antlrVersion")
    antlr("org.antlr:antlr4-runtime:$antlrVersion")
    implementation("com.yuvalshavit:antlr-denter:1.1")

    // logging
    implementation("org.apache.logging.log4j:log4j-api:2.17.1")
    implementation("org.apache.logging.log4j:log4j-to-slf4j:2.24.3")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    runtimeOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.0")

    // other dependencies
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-io:commons-io:2.14.0")
    implementation("com.google.guava:guava:32.0.1-jre")

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

// for use in the bin/tokenize script
tasks.register("saveClasspath") {
    doFirst {
        File("build/classpath.txt").writeText(sourceSets["main"].runtimeClasspath.asPath)
    }
}

////////////////////
// antlr integration
////////////////////

val generatedOutputFilename = "${layout.buildDirectory.get()}/generated/sources/main/java/antlr/org/bashpile/core"

tasks.generateGrammarSource {
    // set the output directory to some arbitrary location in the `/build` directory.
    // by convention `/build/generated/sources/main/java/<generator name>` is often used
    outputDirectory = file(generatedOutputFilename)

    // pass '-package' to make the generator put code in not default space
    arguments = listOf("-package", "org.bashpile.core", "-no-listener", "-visitor")
}

// workaround for an antlr bug, it should be fixed after 4.13.2
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

/////////////////
// GraalVM Native
/////////////////

graalvmNative {
    binaries {
        all {
            resources.autodetect()
        }
        named("main") {
            // more options at https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html#configure-native-image
            imageName.set("bashpile")
            mainClass.set("org.bashpile.core.MainKt")
            sharedLibrary.set(false)

            // additional buildArgs at https://www.graalvm.org/21.3/reference-manual/native-image/Options/

            // From https://stackoverflow.com/questions/72770461/graalvm-native-image-can-not-compile-logback-dependencies
            buildArgs.add("-H:+UnlockExperimentalVMOptions")
            buildArgs.add("-H:ReflectionConfigurationFiles=../../../src/main/resources/reflection-config.json")
        }
    }
}

tasks.named("nativeCompile") {
    dependsOn("test")
}

////////////////////////////////////////////////////////
// system tests - integration tests for the whole system
////////////////////////////////////////////////////////

sourceSets {
    create("intTest") {
        compileClasspath += sourceSets.main.get().output
        compileClasspath += sourceSets.test.get().output
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
    dependsOn("nativeCompile")

    useJUnitPlatform()

    testLogging {
        events("passed")
    }
}

tasks.check { dependsOn(integrationTest) }

////////////////////////////////////
// Deploy binaries for Homebrew Cask
////////////////////////////////////

task<Copy>("deploy") {
    dependsOn("check")

    // copy build/native/nativeCompile/bashpile to bin/bashpile
    from("build/native/nativeCompile")
    include("bashpile")
    into("bin")
}
