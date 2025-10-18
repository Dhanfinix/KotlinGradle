plugins {
    alias(libs.plugins.non.ui)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.dhandev.domain"
}

dependencies {
    implementation(libs.konvert.api)
    ksp(libs.konvert.processor)
    testImplementation(libs.junit)
}