plugins {
    id("popos.android.library")
    id("popos.android.library.jacoco")
    id("popos.android.hilt")
    id("popos.android.room")
}

android {
    namespace = "com.niyaj.core.database"

    defaultConfig {
        testInstrumentationRunner = "com.niyaj.testing.PoposTestRunner"
    }

}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))

    implementation(libs.kotlinx.coroutines.android)

    androidTestImplementation(project(":core:testing"))

    //Moshi
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.moshi)
}