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
 */

package com.niyaj.poposroom.benchmarks.home

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.niyaj.poposroom.benchmarks.PACKAGE_NAME
import com.niyaj.poposroom.benchmarks.restoreDatabase
import com.niyaj.poposroom.benchmarks.startActivityAndGrantPermission
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class ScrollProductsBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollHomeScreenCompilationNone() = scrollHomeScreenProducts(CompilationMode.None())

    private fun scrollHomeScreenProducts(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = compilationMode,
        iterations = 10,
        startupMode = StartupMode.WARM,
        setupBlock = {
            // Start the app
            pressHome()
            restoreDatabase()
            startActivityAndGrantPermission()
        },
    ) {
        homeScreenWaitForContent()
        homeProductsScrollDownUp()
    }
}
