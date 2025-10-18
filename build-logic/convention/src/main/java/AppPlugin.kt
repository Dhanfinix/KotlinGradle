import com.android.build.api.dsl.ApplicationExtension
import com.dhandev.convention.APPS_VERSION
import com.dhandev.convention.BuildType.RELEASE
import com.dhandev.convention.commonConfig
import com.dhandev.convention.commonPlugin
import com.dhandev.convention.compileTargetSdk
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/** Plugin for App module */
open class AppPlugin: Plugin<Project> {
    open val isApp = true
    open val isUi = true
    override fun apply(target: Project) {
        with(target) {
            commonPlugin(isApp = isApp, isUi = isUi)
            extensions.configure<ApplicationExtension> {
                commonConfig(this, isUi)
                defaultConfig.apply {
                    targetSdk = compileTargetSdk
                    versionName = "$APPS_VERSION"
                }
                buildTypes {
                    debug {
                        isMinifyEnabled = false
                    }
                    release {
                        isMinifyEnabled = true
                        signingConfig = signingConfigs.getByName(RELEASE.toString())
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }
            }
        }
    }
}