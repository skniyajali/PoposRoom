import com.android.build.gradle.LibraryExtension
import com.niyaj.samples.apps.popos.configureGradleManagedDevices
import com.niyaj.samples.apps.popos.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("popos.android.library")
                apply("popos.hilt")
                apply("popos.android.library.jacoco")
                apply("io.github.takahirom.roborazzi")
            }

            extensions.configure<LibraryExtension> {
                defaultConfig {
                    testInstrumentationRunner = "com.niyaj.testing.PoposTestRunner"
                }
                testOptions.animationsDisabled = true
                configureGradleManagedDevices(this)
            }

            dependencies {
                add("implementation", project(":core:model"))
                add("implementation", project(":core:ui"))
                add("implementation", project(":core:designsystem"))
                add("implementation", project(":core:data"))
                add("implementation", project(":core:common"))
                add("implementation", project(":core:domain"))
                add("implementation", project(":core:analytics"))

                add("implementation", libs.findLibrary("raamcosta.animation.core").get())
                add("ksp", libs.findLibrary("raamcosta.ksp").get())

                add("implementation", libs.findLibrary("androidx.hilt.navigation.compose").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
                add("implementation", libs.findLibrary("androidx.tracing.ktx").get())
                add("implementation", libs.findLibrary("kotlinx.coroutines.android").get())

                add("testImplementation", kotlin("test"))
                add("testImplementation", libs.findLibrary("hilt.android.testing").get())
                add("testImplementation", libs.findLibrary("robolectric").get())
                add("testImplementation", project(":core:testing"))
                add("testDemoImplementation", project(":core:screenshot-testing"))

                add(
                    "debugImplementation",
                    libs.findLibrary("androidx.compose.ui.testManifest").get(),
                )
                add("debugImplementation", project(":ui-test-hilt-manifest"))

                add("androidTestImplementation", project(":core:testing"))
                add("androidTestImplementation", project(":core:database-test"))
                add("androidTestImplementation", kotlin("test"))
                add(
                    "androidTestImplementation",
                    libs.findLibrary("androidx.test.espresso.core").get(),
                )
                add(
                    "androidTestImplementation",
                    libs.findLibrary("androidx.navigation.testing").get(),
                )
                add("androidTestImplementation", libs.findLibrary("androidx.compose.ui.test").get())
                add("androidTestImplementation", libs.findLibrary("hilt.android.testing").get())
                add(
                    "androidTestImplementation",
                    libs.findLibrary("androidx.lifecycle.runtimeTesting").get(),
                )
            }
        }
    }
}
