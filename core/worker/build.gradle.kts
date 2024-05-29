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
    alias(libs.plugins.popos.android.library)
    alias(libs.plugins.popos.android.library.jacoco)
    alias(libs.plugins.popos.android.hilt)
}

android {
    namespace = "com.niyaj.core.worker"
}

dependencies {

    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:notifications"))
    implementation(libs.hilt.android)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.hilt.ext.work)
    implementation(libs.androidx.work.ktx)
    ksp(libs.hilt.ext.compiler)


    prodImplementation(libs.firebase.cloud.messaging)
    prodImplementation(platform(libs.firebase.bom))

    androidTestImplementation(libs.androidx.work.testing)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(projects.core.testing)
}
