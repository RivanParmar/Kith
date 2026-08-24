plugins {
    alias(libs.plugins.kith.android.library)
    alias(libs.plugins.kith.android.library.compose)
}

android {
    namespace = "com.kith.core.ui"
}

dependencies {
    api(projects.core.designsystem)
    api(projects.core.model)
}