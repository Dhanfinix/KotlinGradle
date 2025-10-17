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
){
    with(pluginManager){
        if (isApp){
            apply(getPluginId("android-application"))
        } else {
            apply(getPluginId("android-library"))
        }
        apply(getPluginId("kotlin-android"))
        apply(getPluginId("ksp"))
        apply(getPluginId("kotlin-compose"))
    }
}

/** Set common configuration for App module and feature module **/
internal fun Project.commonConfig(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    enableFlavorSpecificValue: Boolean
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
            buildConfig = true
        }

        signingConfigs {
            releaseConfig(this@signingConfigs)
        }

        configureFlavors(this) { flavor ->
            if (enableFlavorSpecificValue) {
                setFlavorSpecifiedValue(flavor)
            }
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    configureDependencies()
}

private fun Project.releaseConfig(
    configs: NamedDomainObjectContainer<out ApkSigningConfig>,
) {
    configs.create(BuildType.RELEASE.toString()) {
        // ========================================
        // Load signing credentials from key.properties file
        // This file exists locally but is gitignored (not in remote repo)
        // ========================================
        val keystoreProperties = Properties()
        val keystorePropertiesFile = rootProject.file("key.properties")
        if (keystorePropertiesFile.exists()) {
            keystorePropertiesFile.inputStream().use { keystoreProperties.load(it) }
        }

        // ========================================
        // Priority: -P command line flags (CI) > key.properties file (local dev)
        //
        // CI/CD Pipeline: Uses -PstorePassword, -PkeyPassword, etc. from command line
        // Local Development: Falls back to key.properties file
        // ========================================

        // Store password for the keystore
        storePassword = (project.findProperty("storePassword") as? String)
            ?: keystoreProperties.getProperty("storePassword")

        // Key password within the keystore
        keyPassword = (project.findProperty("keyPassword") as? String)
            ?: keystoreProperties.getProperty("keyPassword")

        // Alias name of the key in the keystore
        keyAlias = (project.findProperty("keyAlias") as? String)
            ?: keystoreProperties.getProperty("keyAlias")

        // Path to the keystore file
        val storeFilePath = (project.findProperty("storeFile") as? String)
            ?: keystoreProperties.getProperty("storeFile")
        if (!storeFilePath.isNullOrEmpty()) {
            storeFile = file(storeFilePath)
        }
    }
}

/** Set common libraries for all module */
private fun Project.configureDependencies() {
    dependencies {
        with(libs()) {
            // Core Compose dependencies
            implementation(findLibrary("androidx-core-ktx").get())
            implementation(findLibrary("androidx-lifecycle-runtime-ktx").get())
            implementation(findLibrary("androidx-activity-compose").get())

            // Compose BOM
            implementation(platform(findLibrary("androidx-compose-bom").get()))

            // Compose UI Bundle
            implementation(findLibrary("androidx-compose-ui").get())
            implementation(findLibrary("androidx-compose-ui-graphics").get())
            implementation(findLibrary("androidx-compose-ui-tooling-preview").get())
            implementation(findLibrary("androidx-compose-material3").get())

            // Testing
            testImplementation(findLibrary("junit").get())
            androidTestImplementation(findLibrary("androidx-junit").get())
            androidTestImplementation(findLibrary("androidx-espresso-core").get())
            androidTestImplementation(platform(findLibrary("androidx-compose-bom").get()))
            androidTestImplementation(findLibrary("androidx-compose-ui-test-junit4").get())

            // Debug tools
            debugImplementation(findLibrary("androidx-compose-ui-tooling").get())
            debugImplementation(findLibrary("androidx-compose-ui-test-manifest").get())
        }
    }
}
