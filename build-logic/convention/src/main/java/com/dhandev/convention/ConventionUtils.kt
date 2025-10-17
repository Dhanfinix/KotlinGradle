package com.dhandev.convention

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

internal fun DependencyHandler.testImplementation(dependencyNotation: Any): Dependency? =
    add("testImplementation", dependencyNotation)

internal fun DependencyHandler.testRuntimeOnly(dependencyNotation: Any): Dependency? =
    add("testRuntimeOnly", dependencyNotation)

internal fun DependencyHandler.implementation(
    dependencyNotation: Provider<MinimalExternalModuleDependency>,
    exclude: (ExternalModuleDependency.() -> Unit)? = null
): Dependency? {
    val dep = add("implementation", dependencyNotation.get())
    if (exclude != null && dep is ExternalModuleDependency) {
        dep.exclude()
    }
    return dep
}

internal fun DependencyHandler.ksp(dependencyNotation: Any): Dependency? =
    add("ksp", dependencyNotation)

internal fun DependencyHandler.kapt(dependencyNotation: Any): Dependency? =
    add("kapt", dependencyNotation)

internal fun DependencyHandler.api(dependencyNotation: Any): Dependency? =
    add("api", dependencyNotation)

internal fun DependencyHandler.androidTestImplementation(dependencyNotation: Any): Dependency? =
    add("androidTestImplementation", dependencyNotation)

internal fun DependencyHandler.debugImplementation(dependencyNotation: Any): Dependency? =
    add("debugImplementation", dependencyNotation)

/** Project extension function to access version catalog libs */
internal fun Project.libs() = extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")  //-> following given name on settings.gradle.kts (:build-logic)

/** Project extension function to access version catalog plugin id */
internal fun Project.getPluginId(alias: String) =
    libs().findPlugin(alias).get().get().pluginId

/** Get version number from version catalog with string return **/
internal fun Project.version(alias: String): String =
    libs().findVersion(alias).get().toString()
/** Get version number from version catalog with integer return **/
internal fun Project.versionInt(alias: String): Int =
    libs().findVersion(alias).get().toString().toInt()