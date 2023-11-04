@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("popos.android.library")
    id("popos.android.library.compose")
    id("popos.android.hilt")
}

android {
    namespace = "com.popos.core.notifications"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.core.ktx)
}