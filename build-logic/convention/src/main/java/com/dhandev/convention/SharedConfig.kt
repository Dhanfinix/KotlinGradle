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
        with(libs()) {
            // Core Compose dependencies
            implementation(findLibrary("androidx-core-ktx").get())
            implementation(findLibrary("androidx-lifecycle-runtime-ktx").get())
            implementation(findLibrary("androidx-activity-compose").get())

            if (isUi){
                // Compose BOM
                implementation(platform(findLibrary("androidx-compose-bom").get()))

                // Compose UI Bundle
                implementation(findLibrary("androidx-compose-ui").get())
                implementation(findLibrary("androidx-compose-ui-graphics").get())
                implementation(findLibrary("androidx-compose-ui-tooling-preview").get())
                implementation(findLibrary("androidx-compose-material3").get())

                androidTestImplementation(platform(findLibrary("androidx-compose-bom").get()))
                androidTestImplementation(findLibrary("androidx-compose-ui-test-junit4").get())

                // Debug tools
                debugImplementation(findLibrary("androidx-compose-ui-tooling").get())
                debugImplementation(findLibrary("androidx-compose-ui-test-manifest").get())
            }

            // Testing
            testImplementation(findLibrary("junit").get())
            androidTestImplementation(findLibrary("androidx-junit").get())
            androidTestImplementation(findLibrary("androidx-espresso-core").get())
        }
    }
}
