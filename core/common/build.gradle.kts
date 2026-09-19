plugins {
    alias(libs.plugins.kith.jvm.library)
    alias(libs.plugins.kith.hilt)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}