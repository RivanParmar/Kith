plugins {
    alias(libs.plugins.kith.android.feature.impl)
    alias(libs.plugins.kith.android.library.compose)
}

android {
    namespace = "com.kith.feature.home.impl"
}

dependencies {
    implementation(projects.feature.home.api)

    implementation(projects.core.model)
    implementation(projects.core.data)
}