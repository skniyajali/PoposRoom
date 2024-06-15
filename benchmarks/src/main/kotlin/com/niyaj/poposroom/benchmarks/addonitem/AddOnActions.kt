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

package com.niyaj.poposroom.benchmarks.addonitem

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import androidx.test.uiautomator.untilHasChildren
import com.niyaj.poposroom.benchmarks.flingElementDownUp
import com.niyaj.poposroom.benchmarks.getHomeAppDrawer
import com.niyaj.poposroom.benchmarks.waitForObjectHomeOnAppDrawer

fun MacrobenchmarkScope.goToAddOnScreenScreen() {
    val drawer = getHomeAppDrawer()
    device.waitForIdle()

    // Wait until the drawer item visible
    waitForObjectHomeOnAppDrawer(By.text("Home"))

    // Click on Products, Categories Expandable
    drawer.wait(untilHasChildren(), 60_000)
    waitForObjectHomeOnAppDrawer(By.res("productCategories"))
    drawer.findObject(By.res("productCategories")).click()

    // wait for on Addon Item visible
    waitForObjectHomeOnAppDrawer(By.res("AddOn Item"))
    device.findObject(By.res("AddOn Item")).click()
}

fun MacrobenchmarkScope.interestsScrollTopicsDownUp() {
    device.wait(Until.hasObject(By.res("interests:topics")), 5_000)
    val topicsList = device.findObject(By.res("interests:topics"))
    device.flingElementDownUp(topicsList)
}

fun MacrobenchmarkScope.interestsWaitForTopics() {
    device.wait(Until.hasObject(By.text("Accessibility")), 30_000)
}

fun MacrobenchmarkScope.interestsToggleBookmarked() {
    val topicsList = device.findObject(By.res("interests:topics"))
    val checkable = topicsList.findObject(By.checkable(true))
    checkable.click()
    device.waitForIdle()
}
