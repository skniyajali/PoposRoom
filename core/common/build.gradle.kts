plugins {
    id("popos.android.library")
    id("popos.android.library.jacoco")
    id("popos.android.hilt")
}

android {
    namespace = "com.niyaj.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
    testImplementation(project(":core:testing"))
}