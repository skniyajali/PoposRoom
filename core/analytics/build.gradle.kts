plugins {
    id("popos.android.library")
    id("popos.android.library.compose")
    id("popos.android.hilt")
}

android {
    namespace = "com.samples.apps.core.analytics"
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.core.ktx)
    implementation(libs.firebase.analytics)
    implementation(libs.kotlinx.coroutines.android)
}
