plugins {
    alias(libs.plugins.kith.android.library)
}

android {
    namespace = "com.kith.core.domain"
}

dependencies {
    api(projects.core.data)
    api(projects.core.model)

    implementation(libs.javax.inject)
}