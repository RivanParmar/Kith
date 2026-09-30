plugins {
    alias(libs.plugins.kith.android.feature.impl)
    alias(libs.plugins.kith.android.library.compose)
}

android {
    namespace = "com.kith.feature.profile.impl"
}

dependencies {
    implementation(projects.feature.profile.api)
    implementation(projects.feature.community.api)

    implementation(projects.core.model)
    implementation(projects.core.data)

    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
}