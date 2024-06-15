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

package com.niyaj.poposroom.benchmarks.cart

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import com.niyaj.poposroom.benchmarks.getHomeAppDrawer
import com.niyaj.poposroom.benchmarks.waitForObjectHomeOnAppDrawer

fun MacrobenchmarkScope.goToCartScreen() {
    val drawer = getHomeAppDrawer()
    device.waitForIdle()

    // Wait until the drawer item visible
    waitForObjectHomeOnAppDrawer(By.text("Home"))
    waitForObjectHomeOnAppDrawer(By.text("View Cart"))

    drawer.findObject(By.res("drawerList"))

    device.findObject(By.res("View Cart")).click()
}
