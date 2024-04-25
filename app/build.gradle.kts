/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @Author Sk Niyaj Ali
 */
import com.niyaj.samples.apps.popos.PoposBuildType
import io.sentry.android.gradle.extensions.InstrumentationFeature
import io.sentry.android.gradle.instrumentation.logcat.LogcatLevel

plugins {
    alias(libs.plugins.popos.android.application)
    alias(libs.plugins.popos.android.application.compose)
    alias(libs.plugins.popos.android.application.flavors)
    alias(libs.plugins.popos.android.application.jacoco)
    alias(libs.plugins.popos.android.hilt)
    alias(libs.plugins.popos.android.application.firebase)
    id("com.google.android.gms.oss-licenses-plugin")
    alias(libs.plugins.androidx.baselineprofile)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.appsweep)
    alias(libs.plugins.sentry)
}

android {
    namespace = libs.versions.namespace.get()

    defaultConfig {
        applicationId = libs.versions.namespace.get()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "com.niyaj.testing.PoposTestRunner"
        manifestPlaceholders.putAll(mapOf("sentryEnvironment" to "production"))
    }

    buildTypes {
        debug {
            applicationIdSuffix = PoposBuildType.DEBUG.applicationIdSuffix
        }

        release {
            isMinifyEnabled = true
            applicationIdSuffix = PoposBuildType.RELEASE.applicationIdSuffix
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            // To publish on the Play store a private signing key is required, but to allow anyone
            // who clones the code to sign and run the release variant, use the debug signing key.
            // TODO: Abstract the signing configuration to a separate file to avoid hardcoding this.
            signingConfig = signingConfigs.getByName("debug")
            // Ensure Baseline Profile is fresh for release builds.
            baselineProfile.automaticGenerationDuringBuild = true
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources {
            excludes.add("META-INF/LICENSE.md")
            excludes.add("META-INF/LICENSE-notice.md")
            excludes.add("DebugProbesKt.bin")
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    appsweep {
        apiKey = "gs_appsweep_yj39WiQ_eZrOIfJnUYYf1KkUkC97yiIO0npd0q6f"
    }

    sentry {
        // Disables or enables debug log output, e.g. for for sentry-cli.
        // Default is disabled.
        debug.set(true)

        // The slug of the Sentry organization to use for uploading proguard mappings/source contexts.
        org.set("skniyajali")

        // The slug of the Sentry project to use for uploading proguard mappings/source contexts.
//        project.set("android")

        // The authentication token to use for uploading proguard mappings/source contexts.
        // WARNING: Do not expose this token in your build.gradle files, but rather set an environment
        // variable and read it into this property.
        authToken.set(System.getenv("SENTRY_AUTH_TOKEN"))

        // The url of your Sentry instance. If you're using SAAS (not self hosting) you do not have to
        // set this. If you are self hosting you can set your URL here
        url = null

        // Disables or enables the handling of Proguard mapping for Sentry.
        // If enabled the plugin will generate a UUID and will take care of
        // uploading the mapping to Sentry. If disabled, all the logic
        // related to proguard mapping will be excluded.
        // Default is enabled.
        includeProguardMapping.set(true)

        // Whether the plugin should attempt to auto-upload the mapping file to Sentry or not.
        // If disabled the plugin will run a dry-run and just generate a UUID.
        // The mapping file has to be uploaded manually via sentry-cli in this case.
        // Default is enabled.
        autoUploadProguardMapping.set(true)

        // Experimental flag to turn on support for GuardSquare's tools integration (Dexguard and External Proguard).
        // If enabled, the plugin will try to consume and upload the mapping file produced by Dexguard and External Proguard.
        // Default is disabled.
        experimentalGuardsquareSupport.set(true)

        // Disables or enables the automatic configuration of Native Symbols
        // for Sentry. This executes sentry-cli automatically so
        // you don't need to do it manually.
        // Default is disabled.
        uploadNativeSymbols.set(false)

        // Whether the plugin should attempt to auto-upload the native debug symbols to Sentry or not.
        // If disabled the plugin will run a dry-run.
        // Default is enabled.
        autoUploadNativeSymbols.set(true)

        // Does or doesn't include the source code of native code for Sentry.
        // This executes sentry-cli with the --include-sources param. automatically so
        // you don't need to do it manually.
        // Default is disabled.
        includeNativeSources.set(false)

        // Generates a JVM (Java, Kotlin, etc.) source bundle and uploads your source code to Sentry.
        // This enables source context, allowing you to see your source
        // code as part of your stack traces in Sentry.
        includeSourceContext.set(true)

        // Configure additional directories to be included in the source bundle which is used for
        // source context. The directories should be specified relative to the Gradle module/project's
        // root. For example, if you have a custom source set alongside 'main', the parameter would be
        // 'src/custom/java'.
        additionalSourceDirsForSourceContext.set(emptySet())

        // Enable or disable the tracing instrumentation.
        // Does auto instrumentation for specified features through bytecode manipulation.
        // Default is enabled.
        tracingInstrumentation {
            enabled.set(true)

            // Specifies a set of instrumentation features that are eligible for bytecode manipulation.
            // Defaults to all available values of InstrumentationFeature enum class.
            features.set(
                setOf(
                    InstrumentationFeature.DATABASE,
                    InstrumentationFeature.FILE_IO,
                    InstrumentationFeature.OKHTTP,
                    InstrumentationFeature.COMPOSE,
                ),
            )

            logcat {
                enabled = true
                minLevel = LogcatLevel.WARNING
            }
        }

        // Enable auto-installation of Sentry components (sentry-android SDK and okhttp, timber, fragment and compose integrations).
        // Default is enabled.
        // Only available v3.1.0 and above.
        autoInstallation {
            enabled.set(true)

            // Specifies a version of the sentry-android SDK and fragment, timber and okhttp integrations.
            //
            // This is also useful, when you have the sentry-android SDK already included into a transitive dependency/module and want to
            // align integration versions with it (if it's a direct dependency, the version will be inferred).
            //
            // NOTE: if you have a higher version of the sentry-android SDK or integrations on the classpath, this setting will have no effect
            // as Gradle will resolve it to the latest version.
            //
            // Defaults to the latest published Sentry version.
            sentryVersion.set("6.33.0")
        }

        // Disables or enables dependencies metadata reporting for Sentry.
        // If enabled, the plugin will collect external dependencies and
        // upload them to Sentry as part of events. If disabled, all the logic
        // related to the dependencies metadata report will be excluded.
        //
        // Default is enabled.
        //
        includeDependenciesReport.set(true)
    }
}

dependencies {

    implementation(project(":feature:account"))
    implementation(project(":feature:addonitem"))
    implementation(project(":feature:address"))
    implementation(project(":feature:cart"))
    implementation(project(":feature:cart_selected"))
    implementation(project(":feature:cartorder"))
    implementation(project(":feature:category"))
    implementation(project(":feature:charges"))
    implementation(project(":feature:customer"))
    implementation(project(":feature:employee"))
    implementation(project(":feature:employee_payment"))
    implementation(project(":feature:employee_absent"))
    implementation(project(":feature:expenses"))
    implementation(project(":feature:home"))
    implementation(project(":feature:order"))
    implementation(project(":feature:print_order"))
    implementation(project(":feature:product"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:printer_info"))
    implementation(project(":feature:reports"))
    implementation(project(":feature:daily_market"))


    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:worker"))
    implementation(project(":core:analytics"))

    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.tracing.ktx)
    implementation(libs.androidx.window.core)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.coil.kt)

    ksp(libs.hilt.compiler)

    debugImplementation(libs.androidx.compose.ui.testManifest)
    debugImplementation(projects.uiTestHiltManifest)

    kspTest(libs.hilt.compiler)

    // Sentry
    implementation(libs.sentry.android)
    implementation(libs.sentry.compose.android)

    //RaamCosta Library
    implementation(libs.raamcosta.animation.core)
    ksp(libs.raamcosta.ksp)

    implementation(libs.navigation.bar)

    // Timber
    implementation(libs.timber)

    testImplementation(projects.core.testing)
    testImplementation(libs.androidx.compose.ui.test)
    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.androidx.work.testing)

    testDemoImplementation(libs.robolectric)
    testDemoImplementation(libs.roborazzi)
    testDemoImplementation(projects.core.screenshotTesting)


    androidTestImplementation(projects.core.testing)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.hilt.android.testing)

    baselineProfile(projects.benchmarks)
}

baselineProfile {
    // Don't build on every iteration of a full assemble.
    // Instead enable generation directly for the release build variant.
    automaticGenerationDuringBuild = false
}

dependencyGuard {
    configuration("prodReleaseRuntimeClasspath")
}