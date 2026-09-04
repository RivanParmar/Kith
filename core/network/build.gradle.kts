plugins {
    alias(libs.plugins.kith.android.library)
    alias(libs.plugins.kith.hilt)
    id("kotlinx-serialization")
}

android {
    buildFeatures {
        buildConfig = true
    }
    namespace = "com.kith.core.network"
}

dependencies {
    api(projects.core.model)

    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.auth)
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.storage)
    implementation(libs.ktor.android)
}