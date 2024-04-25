plugins {
    alias(libs.plugins.popos.android.library)
    alias(libs.plugins.popos.android.library.compose)
    alias(libs.plugins.popos.android.hilt)
}

android {
    namespace = "com.niyaj.poposroom.core.screenshottesting"
}

dependencies {
    api(libs.roborazzi)
    implementation(libs.androidx.compose.ui.test)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui.test)
    implementation(libs.robolectric)
    implementation(projects.core.common)
    implementation(projects.core.designsystem)
}
