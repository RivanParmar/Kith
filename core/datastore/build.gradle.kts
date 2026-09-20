plugins {
    alias(libs.plugins.kith.android.library)
    alias(libs.plugins.kith.hilt)
}

android {
    namespace = "com.kith.core.datastore"
}

dependencies {
    api(libs.androidx.dataStore)
    api(projects.core.datastoreProto)
    api(projects.core.model)

    implementation(projects.core.common)
}