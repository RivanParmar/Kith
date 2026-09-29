plugins {
    alias(libs.plugins.kith.android.library)
    alias(libs.plugins.kith.hilt)
}

android {
    namespace = "com.kith.core.notifications"
}

dependencies {
    api(projects.core.model)

    implementation(projects.core.common)
}