package com.dhandev.convention

import com.android.build.api.dsl.ApkSigningConfig
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.exclude
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

/** Apply common plugin for application and feature module */
internal fun Project.commonPlugin(
    isApp: Boolean,
    isUi: Boolean
){
    with(pluginManager){
        if (isApp){
            apply(getPluginId("android-application"))
        } else {
            apply(getPluginId("android-library"))
        }
        if (isUi){
            apply(getPluginId("kotlin-compose"))
        }
        apply(getPluginId("kotlin-android"))
    }
}

/** Set common configuration for App module and feature module **/
internal fun Project.commonConfig(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    isUi: Boolean
) {
    commonExtension.apply {
        compileSdk = compileTargetSdk

        defaultConfig {
            minSdk = projectMinSdk
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        buildFeatures {
            if (isUi){
                compose = true
            }
            buildConfig = true
        }

        signingConfigs {
            releaseConfig(this@signingConfigs)
        }

        configureFlavors(this) { flavor ->
            setFlavorSpecifiedValue(flavor)
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    configureDependencies(isUi)
}

private fun Project.releaseConfig(
    configs: NamedDomainObjectContainer<out ApkSigningConfig>,
) {
    configs.create(BuildType.RELEASE.toString()) {
        val keystoreProperties = Properties()
        val keystorePropertiesFile = rootProject.file("key.properties")
        if (keystorePropertiesFile.exists()) {
            keystorePropertiesFile.inputStream().use { keystoreProperties.load(it) }
        }
        // Store password for the keystore
        storePassword = keystoreProperties.getProperty("storePassword")

        // Key password within the keystore
        keyPassword = keystoreProperties.getProperty("keyPassword")

        // Alias name of the key in the keystore
        keyAlias = keystoreProperties.getProperty("keyAlias")

        // Path to the keystore file
        val storeFilePath = keystoreProperties.getProperty("storeFile")
        if (!storeFilePath.isNullOrEmpty()) {
            storeFile = file(storeFilePath)
        }
    }
}

/** Set common libraries for all module */
private fun Project.configureDependencies(isUi: Boolean) {
    dependencies {
        // Core dependencies
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.activity.compose)

        if (isUi) {
            // Compose BOM
            implementation(platform(libs.androidx.compose.bom))

            // Compose UI Bundle
            implementation(libs.androidx.compose.ui.ui)
            implementation(libs.androidx.compose.ui.graphics)
            implementation(libs.androidx.compose.ui.tooling.preview)
            implementation(libs.androidx.compose.material3)

            // Compose Testing
            androidTestImplementation(platform(libs.androidx.compose.bom))
            androidTestImplementation(libs.androidx.compose.ui.test.junit4)

            // Debug tools
            debugImplementation(libs.androidx.compose.ui.tooling)
            debugImplementation(libs.androidx.compose.ui.test.manifest)
        }

        // Unit testing
        testImplementation(libs.junit)

        // Android instrumented testing
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
    }
}
