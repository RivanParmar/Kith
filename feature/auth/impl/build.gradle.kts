plugins {
    alias(libs.plugins.kith.android.feature.impl)
    alias(libs.plugins.kith.android.library.compose)
}

android {
    namespace = "com.kith.feature.auth.impl"
}

dependencies {
    implementation(projects.feature.auth.api)

    implementation(projects.core.data)
}