plugins {
    alias(libs.plugins.popos.android.feature)
    alias(libs.plugins.popos.android.library.jacoco)
}

android {
    namespace = "com.niyaj.feature.printer"
}

dependencies {
    implementation(libs.pos.printer)
}