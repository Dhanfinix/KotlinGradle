import com.android.build.gradle.LibraryExtension
import com.dhandev.convention.BuildType.RELEASE
import com.dhandev.convention.commonConfig
import com.dhandev.convention.commonPlugin
import com.dhandev.convention.compileTargetSdk
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/** Plugin for feature module */
open class ModulePlugin: AppPlugin() {
    override val isApp = false
    override val enableFlavorSpecificValue = false
    override fun apply(target: Project) {
        with(target) {
            commonPlugin(isApp)
            extensions.configure<LibraryExtension> {
                commonConfig(this, enableFlavorSpecificValue)
                defaultConfig.apply {
                    targetSdk = compileTargetSdk
                    consumerProguardFiles("consumer-rules.pro")
                }
                buildTypes {
                    release {
                        isMinifyEnabled = false // only app module need this to be true
                            signingConfigs.getByName(RELEASE.toString())
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