import java.util.Properties

plugins {
    alias(libs.plugins.kith.android.feature.impl)
    alias(libs.plugins.kith.android.library.compose)
}

val localProperties = Properties()
val localPropertiesFile = project.rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "com.kith.feature.paywall.impl"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        val revenueCatKey = System.getenv("REVENUECAT_API_KEY")
            ?: System.getenv("REVENUECAT_GOOGLE_API_KEY")
            ?: localProperties.getProperty("REVENUECAT_API_KEY")
            ?: localProperties.getProperty("REVENUECAT_GOOGLE_API_KEY")
            ?: ""
        buildConfigField("String", "REVENUECAT_API_KEY", "\"$revenueCatKey\"")
    }
}

dependencies {
    implementation(projects.feature.paywall.api)

    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.database)

    implementation(libs.androidx.activity.compose)
    implementation(libs.revenuecat.purchases)
}
