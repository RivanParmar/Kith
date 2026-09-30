plugins {
    alias(libs.plugins.kith.android.feature.impl)
    alias(libs.plugins.kith.android.library.compose)
}

android {
    namespace = "com.kith.feature.browse.impl"
}

dependencies {
    implementation(projects.feature.browse.api)
    implementation(projects.feature.post.api)
    implementation(projects.feature.profile.api)

    implementation(projects.core.data)
}