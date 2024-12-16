plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    id("androidx.navigation.safeargs") version "2.8.5" apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
