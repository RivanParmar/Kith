plugins {
    alias(libs.plugins.kith.android.feature.api)
}

android {
    namespace = "com.kith.feature.onboarding.api"
}

dependencies {
    api(projects.core.navigation)
}