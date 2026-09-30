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

    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)

    implementation("io.coil-kt.coil3:coil-network-okhttp:3.6.3")
}