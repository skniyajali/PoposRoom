plugins {
    alias(libs.plugins.popos.android.library)
    alias(libs.plugins.popos.android.library.jacoco)
    alias(libs.plugins.popos.android.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "com.niyaj.core.data"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    api(projects.core.common)
    api(projects.core.database)
    api(projects.core.model)

    implementation(projects.core.analytics)
    implementation(projects.core.notifications)

    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.timber)

    testImplementation(projects.core.testing)
}