plugins {
    id("popos.android.feature")
    id("popos.android.library.compose")
    id("popos.android.library.jacoco")
}

android {
    namespace = "com.niyaj.printer_info"

    ksp {
        arg("compose-destinations.moduleName", "printer_info")
        arg("compose-destinations.mode", "navgraphs")
        arg("compose-destinations.useComposableVisibility", "true")
    }
}

dependencies {
    implementation(libs.accompanist.permissions)
    implementation(libs.pos.printer)

    //RaamCosta Library
    implementation(libs.raamcosta.animation.core)
    ksp(libs.raamcosta.ksp)
}