@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.appsweep)
    alias(libs.plugins.androidx.baselineprofile)
    alias(libs.plugins.ksp)
    id(libs.plugins.kotlin.kapt.get().pluginId)
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
        testInstrumentationRunner = "com.niyaj.popos.HiltTestRunner"
        manifestPlaceholders.putAll(mapOf("sentryEnvironment" to "production"))
        vectorDrawables {
            useSupportLibrary = true
        }

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }



    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.4"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
            excludes += "DebugProbesKt.bin"
        }
    }

    sourceSets {
        getByName("main") {
            java.srcDir("src/main/kotlin")
        }
        getByName("test") {
            java.srcDir("src/test/kotlin")
        }
        getByName("androidTest") {
            java.srcDir("src/androidTest/kotlin")
        }
    }

    hilt {
        enableAggregatingTask = true
    }
}

dependencies {
    implementation(libs.core.ktx)

//    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
//    implementation(libs.material)
    implementation(libs.material3)
    implementation(libs.material.icons)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3.window.size)
    implementation(libs.activity.compose)

    //Startup & Splash screen
    implementation(libs.startup)
    implementation(libs.splashscreen)

    implementation(libs.runtime.livedata)

    // ViewModel
    implementation(libs.viewmodel.ktx)
    implementation(libs.viewmodel.compose)
    implementation(libs.runtime.compose)
    implementation(libs.runtime.ktx)
    // Saved state module for ViewModel
    implementation(libs.viewmodel.savedstate)
    // Annotation processor
    implementation(libs.common.java8)

    // Compose dependencies
    implementation(libs.navigation.compose)

    // Kotlin + coroutines
    implementation(libs.work.runtime.ktx)
    androidTestImplementation(libs.work.testing)

    //Accompanist
    implementation(libs.flowlayout)
    implementation(libs.systemuicontroller)
    implementation(libs.permissions)
    implementation(libs.swiperefresh)
    implementation(libs.placeholder.material)
    implementation(libs.pager)
    implementation(libs.pager.indicators)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // For testing
    testImplementation(libs.coroutines.test)
    androidTestImplementation(libs.coroutines.test)

    //Hilt Work
    implementation(libs.hilt.work)
    kapt(libs.hilt.compiler)

    implementation(libs.hilt.navigation.compose)
    kapt(libs.hilt.android)

    // Dagger & Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.dagger.compiler)

    // For testing
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.dagger.compiler)

    testImplementation(libs.hilt.android.testing)
    kaptTest(libs.hilt.dagger.compiler)

    // Timber
    implementation(libs.timber)

    //RevealSwipe
    implementation(libs.revealswipe)

    //Pos.printer
    implementation(libs.pos.printer)

    //Realm
    // implementation(libs.realm.library.base)

    // Room
    implementation(libs.room.runtime)
    ksp(libs.room.complier)

    // To use Kotlin annotation processing tool (kapt)
    ksp(libs.room.complier)

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.room.ktx)

    // optional - Test helpers
    testImplementation(libs.room.testing)

    // optional - Paging 3 Integration
    implementation(libs.room.paging)

    // Paging
    implementation(libs.paging.runtime)
    // optional - Jetpack Compose integration
    implementation(libs.paging.compose)

    // debugImplementation because LeakCanary should only run in debug builds.
    debugImplementation(libs.leakcanary)

    //RaamCosta Library
    implementation(libs.raamcosta.core)
    ksp(libs.raamcosta.ksp)

    //ProfileInstaller
    implementation(libs.profileinstaller)

    // Local unit tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
    testImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.androidx.test.runner)

    testImplementation(libs.androidx.arch.core.testing)
    androidTestImplementation(libs.androidx.arch.core.testing)

    androidTestImplementation(libs.ui.test.junit)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    // Truth
    testImplementation(libs.truth)
    androidTestImplementation(libs.truth)

    //Mockk
    androidTestImplementation(libs.mockk.android)
    testImplementation(libs.mockk)

    //ACRA Logger
    implementation(libs.acra.mail)
    implementation(libs.acra.toast)

    //Sentry
    implementation(libs.sentry.android)
    implementation(libs.sentry.compose.android)

    // Vanpara DatePicker
    implementation(libs.dialog.datetime)

    //Baseline Profile
//    "baselineProfile"(project(mapOf("path" to ":benchmark")))
}

kapt {
    correctErrorTypes = true
}