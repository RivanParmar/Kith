plugins {
    alias(libs.plugins.kith.android.feature.impl)
    alias(libs.plugins.kith.android.library.compose)
}

android {
    namespace = "com.kith.feature.community.impl"
}

dependencies {
    implementation(projects.feature.community.api)

    implementation(projects.core.data)

    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
    implementation(libs.coil.network.okhttp)
}