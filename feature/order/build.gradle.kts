@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("popos.android.feature")
    id("popos.android.library.compose")
    id("popos.android.library.jacoco")
    id("popos.android.hilt")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.niyaj.feature.order"

    ksp {
        arg("compose-destinations.moduleName", "order")
        arg("compose-destinations.mode", "navgraphs")
        arg("compose-destinations.useComposableVisibility", "true")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.material3)
    implementation(libs.accompanist.swiperefresh)
    implementation(libs.accompanist.permissions)
    implementation(libs.revealswipe)
    implementation(libs.dialog.core)
    implementation(libs.dialog.datetime)
    implementation(libs.pos.printer)


    //RaamCosta Library
    implementation(libs.raamcosta.animation.core)
    ksp(libs.raamcosta.ksp)
}