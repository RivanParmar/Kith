plugins {
    alias(libs.plugins.kith.android.feature.impl)
    alias(libs.plugins.kith.android.library.compose)
}

android {
    namespace = "com.kith.feature.profile.impl"
}

dependencies {
    implementation(projects.feature.profile.api)

    implementation(projects.core.model)
    implementation(projects.core.data)

    implementation(libs.androidx.activity.compose)
}