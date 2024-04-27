plugins {
    alias(libs.plugins.popos.android.library)
    alias(libs.plugins.popos.android.library.compose)
    alias(libs.plugins.popos.android.library.jacoco)
    alias(libs.plugins.popos.android.hilt)
}

android {
    namespace = "com.niyaj.core.ui"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    api(libs.androidx.metrics)
    api(projects.core.analytics)
    api(projects.core.designsystem)
    api(projects.core.model)
    api(projects.core.common)
    api(libs.androidx.tracing.ktx)

    androidTestImplementation(projects.core.testing)

    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.core.ktx)
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
    implementation(libs.navigation.bar)
    implementation(libs.lottie.compose)
}