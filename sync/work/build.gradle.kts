plugins {
    alias(libs.plugins.kith.android.library)
    alias(libs.plugins.kith.hilt)
}

android {
    namespace = "com.kith.sync"
}

dependencies {
    ksp(libs.hilt.ext.compiler)

    implementation(libs.androidx.work.ktx)
    implementation(libs.hilt.ext.work)
    implementation(projects.core.data)
    implementation(projects.core.notifications)

    androidTestImplementation(libs.androidx.work.testing)
    androidTestImplementation(libs.hilt.android.testing)
}