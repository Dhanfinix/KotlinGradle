plugins {
    alias(libs.plugins.non.ui)
}

android {
    namespace = "com.dhandev.data"
}

dependencies {
    implementation(libs.retrofit)
}