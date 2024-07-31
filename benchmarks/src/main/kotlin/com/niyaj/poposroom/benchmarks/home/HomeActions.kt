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

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import androidx.test.uiautomator.untilHasChildren
import com.niyaj.poposroom.benchmarks.flingElementDownUp
import com.niyaj.poposroom.benchmarks.waitAndFindObject

fun MacrobenchmarkScope.homeScreenWaitForContent() {
    // Wait until content is loaded by checking if topics are loaded
    device.wait(Until.gone(By.desc("loadingIndicator")), 5_000)
    // Sometimes, the loading wheel is gone, but the content is not loaded yet
    // So we'll wait here for topics to be sure
    val obj = device.waitAndFindObject(By.res("homeScreenProducts"), 10_000)
    // Timeout here is quite big, because sometimes data loading takes a long time!
    obj.wait(untilHasChildren(), 60_000)
}

fun MacrobenchmarkScope.homeProductsScrollDownUp() {
    val feedList = device.findObject(By.res("homeScreenProducts"))
    device.flingElementDownUp(feedList)
}
