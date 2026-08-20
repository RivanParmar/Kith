import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("kith.android.library")
                apply("kith.android.hilt")
            }

            extensions.configure<LibraryExtension> {
                testOptions.animationsDisabled = true
            }

            dependencies {

            }
        }
    }
}