import com.niyaj.samples.apps.popos.PoposBuildType

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("popos.android.application")
    id("popos.android.application.flavors")
    id("popos.android.application.jacoco")
    id("popos.android.hilt")
    id("jacoco")
    alias(libs.plugins.appsweep)
    alias(libs.plugins.ksp)
}

android {
    namespace = libs.versions.namespace.get()
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = libs.versions.namespace.get()
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
        testInstrumentationRunner = "com.niyaj.testing.PoposTestRunner"
        manifestPlaceholders.putAll(mapOf("sentryEnvironment" to "production"))
    }

    buildTypes {
        val debug by getting {
            applicationIdSuffix = PoposBuildType.DEBUG.applicationIdSuffix
        }
        val release by getting {
            isMinifyEnabled = true
            applicationIdSuffix = PoposBuildType.RELEASE.applicationIdSuffix
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            // To publish on the Play store a private signing key is required, but to allow anyone
            // who clones the code to sign and run the release variant, use the debug signing key.
            // TODO: Abstract the signing configuration to a separate file to avoid hardcoding this.
            signingConfig = signingConfigs.getByName("debug")
        }

        val benchmark by creating {
            // Enable all the optimizations from release build through initWith(release).
            initWith(release)
            matchingFallbacks.add("release")
            // Debug key signing is available to everyone.
            signingConfig = signingConfigs.getByName("debug")
            // Only use benchmark proguard rules
            proguardFiles("benchmark-rules.pro")
            isMinifyEnabled = true
            applicationIdSuffix = PoposBuildType.BENCHMARK.applicationIdSuffix
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
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
        apiKey = "gs_appsweep_9LOhFix_p6bow4wYcoXwSfLLe6U7ZPDT5HDoGHxw"
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

    androidTestImplementation(project(":core:testing"))
    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(libs.accompanist.testharness)
    androidTestImplementation(kotlin("test"))
    debugImplementation(libs.androidx.compose.ui.testManifest)
//    debugImplementation(project(":ui-test-hilt-manifest"))

    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.permissions)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.window.manager)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.kotlinx.coroutines.guava)

    //RaamCosta Library
    implementation(libs.raamcosta.animation.core)
    ksp(libs.raamcosta.ksp)

    // Timber
    implementation(libs.timber)

    //Google Play Play Integrity API
    implementation(libs.play.integrity)

    // Google Play In-App Updates API
    implementation(libs.play.app.update)
    implementation(libs.play.app.update.ktx)

    // zxing QR code library
    implementation(libs.zxing.core)

    // Play GMS Scanner library
    implementation(libs.play.gms.scanner)

    // Play Service Base
    implementation(libs.play.service)
}