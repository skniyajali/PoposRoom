plugins {
    alias(libs.plugins.popos.android.library)
    alias(libs.plugins.popos.android.library.jacoco)
    alias(libs.plugins.popos.android.hilt)
}

android {
    namespace = "com.niyaj.core.common"
}

dependencies {
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}