/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
plugins {
    alias(libs.plugins.popos.android.feature)
    alias(libs.plugins.popos.android.library.compose)
    alias(libs.plugins.popos.android.library.jacoco)
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.niyaj.feature.cart"

    ksp {
        arg("compose-destinations.moduleName", "cart")
        arg("compose-destinations.mode", "navgraphs")
        arg("compose-destinations.useComposableVisibility", "true")
    }
}

dependencies {
    implementation(projects.feature.printOrder)

    implementation(libs.accompanist.permissions)

    //RaamCosta Library
    implementation(libs.raamcosta.animation.core)
    ksp(libs.raamcosta.ksp)

    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.robolectric)
    testImplementation(projects.core.testing)
    testImplementation(projects.core.screenshotTesting)
    testDemoImplementation(libs.roborazzi)

    androidTestImplementation(projects.core.testing)
}