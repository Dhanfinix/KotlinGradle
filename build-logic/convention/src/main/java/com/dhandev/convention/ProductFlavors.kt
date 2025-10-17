package com.dhandev.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor
import com.android.build.api.dsl.VariantDimension
import java.io.File
import java.util.Properties

@Suppress("EnumEntryName")
enum class FlavorDimension {
    contentType
}

/** The content for the app can either come from local static data which is useful for demo
* purposes, or from a production backend server which supplies up-to-date, real content.
* These two product flavors reflect this behaviour. */
@Suppress("EnumEntryName")
enum class IdmFlavor(
    val dimension: FlavorDimension = FlavorDimension.contentType,
    val appName: String = NAME_PROD,
    val buildNumber: Int = APPS_PRODUCTION_BUILD_NUMBER,
    val appId: String = PROD_ID,
) {
    development(
        appName = NAME_DEV,
        buildNumber = APPS_STAGING_BUILD_NUMBER,
        appId = DEV_ID,
    ),
    uat(
        appName = NAME_UAT,
        buildNumber = APPS_UAT_BUILD_NUMBER,
        appId = UAT_ID,
    ),
    to(
        appName = NAME_TO,
        buildNumber = APPS_TO_BUILD_NUMBER,
        appId = TO_ID,
    ),
    prod(),
}

val mapsApiKeyProperties = Properties().apply {
    load(File("local.properties").reader())
}

fun configureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: IdmFlavor) -> Unit = {},
) {
    commonExtension.apply {
        FlavorDimension.values().forEach { flavorDimension ->
            flavorDimensions += flavorDimension.name
        }

        productFlavors {
            IdmFlavor.values().forEach { flavor ->
                register(flavor.name) {
                    dimension = flavor.dimension.name
                    flavorConfigurationBlock(this, flavor)

                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        applicationId = flavor.appId
                        versionCode = flavor.buildNumber
                    }

                    // Common build config fields
                    buildConfigField("String", "BUILD_NUMBER", "\"${flavor.buildNumber}\"")
                    buildConfigField("String", "VERSION_NAME", "\"${APPS_VERSION}\"")
                    buildConfigField("boolean", "isPhaseTwo", "true")

                    // Common res values
                    resValue("string", "app_name", flavor.appName)
                }
            }
        }
    }
}

fun VariantDimension.setFlavorSpecifiedValue(flavor: IdmFlavor){
    // Flavor-specific configurations
    when (flavor) {
        IdmFlavor.development -> {
            // API URLs
            buildConfigField("String", "BASE_API_URL", "\"${API_BASE_URL_STAGING}\"")
        }

        IdmFlavor.uat -> {
            // API URLs
            buildConfigField("String", "BASE_API_URL", "\"${API_BASE_URL_UAT}\"")
        }

        IdmFlavor.to -> {
            // API URLs
            buildConfigField("String", "BASE_API_URL", "\"${API_PATH_URL_TO}\"")
        }

        IdmFlavor.prod -> {
            // API URLs
            buildConfigField("String", "BASE_API_URL", "\"${API_BASE_URL_PRODUCTION}\"")
        }
    }
}
