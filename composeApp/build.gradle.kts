plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

// this do anything?
group = "org.bashpile.core"
version = "1.0.0"

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
        }
        desktopMain.dependencies {
        }
    }
}
