plugins {
    alias(libs.plugins.kith.android.library)
    alias(libs.plugins.kith.android.room)
    alias(libs.plugins.kith.hilt)
}

android {
    namespace = "com.kith.core.database"
}

dependencies {
    api(projects.core.model)
}