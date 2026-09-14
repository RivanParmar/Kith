import com.kith.KithBuildType

plugins {
    alias(libs.plugins.kith.android.application)
    alias(libs.plugins.kith.android.application.compose)
    alias(libs.plugins.kith.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.kith"

    defaultConfig {
        applicationId = "com.kith"
        versionCode = 1
        versionName = "0.0.1" // X.Y.Z; X = Major, Y = minor, Z = Patch level

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = KithBuildType.DEBUG.applicationIdSuffix
        }
        release {
            optimization {
                enable = false
            }
        }
    }
}

dependencies {
    implementation(projects.feature.auth.api)
    implementation(projects.feature.auth.impl)
    implementation(projects.feature.onboarding.api)
    implementation(projects.feature.onboarding.impl)

    implementation(projects.core.ui)
    implementation(projects.core.designsystem)
    implementation(projects.core.model)
    implementation(projects.sync.work)

    implementation(libs.androidx.core.splashscreen)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}