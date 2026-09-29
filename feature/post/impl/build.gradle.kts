plugins {
    alias(libs.plugins.kith.android.feature.impl)
    alias(libs.plugins.kith.android.library.compose)
}

android {
    namespace = "com.kith.feature.post.impl"
}

dependencies {
    implementation(projects.feature.post.api)

    implementation(projects.core.data)

    implementation(projects.core.domain)
}