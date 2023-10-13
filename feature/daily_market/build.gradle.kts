@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("popos.android.feature")
    id("popos.android.library.compose")
    id("popos.android.library.jacoco")
    id("popos.android.hilt")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.niyaj.feature.daily_market"

    ksp {
        arg("compose-destinations.moduleName", "daily_market")
        arg("compose-destinations.mode", "navgraphs")
        arg("compose-destinations.useComposableVisibility", "true")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.material3)
    implementation(libs.accompanist.flowlayout)
    implementation(libs.accompanist.permissions)
    implementation(libs.dialog.core)
    implementation(libs.dialog.datetime)
    implementation(libs.saket.swipe)


    //RaamCosta Library
    implementation(libs.raamcosta.animation.core)
    ksp(libs.raamcosta.ksp)
}