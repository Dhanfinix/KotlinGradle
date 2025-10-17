package com.dhandev.convention

/**
 * Put build constant here that can differ based on the build flavor
 * (ex: Base URL)
 * Don't forget to Sync gradle files after change!
 */
const val projectMinSdk = 24
const val compileTargetSdk = 36
const val NAME_PROD = "Kotlin Gradle"
const val NAME_DEV = "Kotlin Gradle Dev"
const val NAME_UAT = "Kotlin Gradle UAT"
const val NAME_TO = "Kotlin Gradle TO"

const val PROD_ID = "com.dhandev.kotlin.gradle"
const val DEV_ID = "com.dhandev.kotlin.gradle.dev"
const val UAT_ID = "com.dhandev.kotlin.gradle.uat"
const val TO_ID = "com.dhandev.kotlin.gradle.to"

// ==============================================================================
// APP CONFIGURATION
// ==============================================================================
const val APPS_VERSION = 25101001
const val APPS_STAGING_BUILD_NUMBER = 547
const val APPS_UAT_BUILD_NUMBER = 1
const val APPS_TO_BUILD_NUMBER = 1
const val APPS_PRODUCTION_BUILD_NUMBER = 1

// ==============================================================================
// DATABASE CONFIGURATION
// ==============================================================================
const val APPS_DB_VERSION = 1
const val APPS_DB_PASS_PHRASE = "edtsB!ru2022"
const val APPS_DB_NAME = "klikIdmAppDb"

const val APPS_DB_V1_NAME = "RKStorage"
const val APPS_DB_V1_VERSION = 1

// ==============================================================================
// API ENDPOINTS (HEX ENCODED)
// ==============================================================================
const val API_BASE_URL_STAGING = "www.google.com"
const val API_BASE_URL_UAT = "www.google.com"
const val API_BASE_URL_PRODUCTION = "www.google.com"
const val API_PATH_URL_TO = "www.google.com"