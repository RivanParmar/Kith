plugins {
    alias(libs.plugins.kith.android.feature.impl)
    alias(libs.plugins.kith.android.library.compose)
}

android {
    namespace = "com.kith.feature.leaderboard.impl"
}

dependencies {
    implementation(projects.feature.leaderboard.api)

    implementation(projects.core.data)
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
}