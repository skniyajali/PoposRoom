plugins {
    alias(libs.plugins.popos.android.library)
    alias(libs.plugins.popos.android.library.jacoco)
    alias(libs.plugins.popos.android.hilt)
    alias(libs.plugins.popos.android.room)
}

android {
    namespace = "com.niyaj.core.database"

    defaultConfig {
        testInstrumentationRunner = "com.niyaj.testing.PoposTestRunner"
    }

}

dependencies {
    api(project(":core:model"))
    api(project(":core:common"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.collections.immutable)

    androidTestImplementation(project(":core:testing"))
}