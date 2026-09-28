plugins {
    alias(libs.plugins.kith.android.library)
    alias(libs.plugins.kith.hilt)
}

android {
    namespace = "com.kith.core.push"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.notifications)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.cloud.messaging)
}