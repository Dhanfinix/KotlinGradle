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
class AppPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            commonPlugin(isApp = true, isUi = true)
            extensions.configure<ApplicationExtension> {
                commonConfig(this, true)
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