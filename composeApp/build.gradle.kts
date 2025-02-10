plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

// this do anything?
group = "org.bashpile.core"
version = "1.0.0"

kotlin {
    // Multiplatform targets
    jvm("desktop")
    macosX64()
    macosArm64()
    linuxX64()
    linuxArm64()
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
        }
        desktopMain.dependencies {
        }
    }
}
