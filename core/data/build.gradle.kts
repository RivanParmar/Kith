plugins {
    alias(libs.plugins.kith.android.library)
    alias(libs.plugins.kith.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "com.kith.core.data"
}

dependencies {
    api(projects.core.common)
    api(projects.core.database)
    api(projects.core.datastore)
    api(projects.core.network)
}