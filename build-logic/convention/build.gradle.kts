import org.gradle.initialization.DependenciesAccessors
import org.gradle.kotlin.dsl.support.serviceOf

plugins {
    `kotlin-dsl`
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}
dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    // this made libs call possible inside the plugin, no need findLibrary
    gradle.serviceOf<DependenciesAccessors>().classes.asFiles.forEach {
        compileOnly(files(it.absolutePath))
    }
}
gradlePlugin {
    plugins {
        register("appPlugin") {
            id = libs.plugins.app.get().pluginId
            implementationClass = "AppPlugin"
        }
        register("modulePlugin") {
            id = libs.plugins.module.get().pluginId
            implementationClass = "ModulePlugin"
        }
        register("nonUiPlugin"){
            id = libs.plugins.non.ui.get().pluginId
            implementationClass = "NonUiPlugin"
        }
    }
}