import com.android.build.gradle.LibraryExtension
import com.dhandev.convention.commonConfig
import com.dhandev.convention.commonPlugin
import com.dhandev.convention.compileTargetSdk
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/** Plugin for feature module */
open class ModulePlugin: Plugin<Project> {
    open val isUi = true
    override fun apply(target: Project) {
        with(target) {
            commonPlugin(false, isUi)
            extensions.configure<LibraryExtension> {
                commonConfig(this, isUi)
                defaultConfig.apply {
                    targetSdk = compileTargetSdk
                    consumerProguardFiles("consumer-rules.pro")
                }
            }
        }
    }
}