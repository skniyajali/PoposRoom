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

package com.niyaj.poposroom.benchmarks

import android.Manifest.permission
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until

/**
 * Because the app under test is different from the one running the instrumentation test,
 * the permission has to be granted manually by either:
 *
 * - tapping the Allow button
 *    ```kotlin
 *    val obj = By.text("Allow")
 *    val dialog = device.wait(Until.findObject(obj), TIMEOUT)
 *    dialog?.let {
 *        it.click()
 *        device.wait(Until.gone(obj), 5_000)
 *    }
 *    ```
 * - or (preferred) executing the grant command on the target package.
 */
fun MacrobenchmarkScope.allowNotifications() {
    if (SDK_INT >= TIRAMISU) {
        val command = "pm grant $packageName ${permission.POST_NOTIFICATIONS}"
        device.executeShellCommand(command)
    }
}

fun MacrobenchmarkScope.allowAllPermission() {
    if (SDK_INT >= TIRAMISU) {
        val command = "pm grant $packageName ${permission.POST_NOTIFICATIONS}"
        device.executeShellCommand(command)
    }

    val blePer = "pm grant $packageName ${permission.BLUETOOTH}"
    val bleAdmin = "pm grant $packageName ${permission.BLUETOOTH_ADMIN}"

    if (SDK_INT >= Build.VERSION_CODES.S) {
        val bleConnect = "pm grant $packageName ${permission.BLUETOOTH_CONNECT}"
        val bleScan = "pm grant $packageName ${permission.BLUETOOTH_SCAN}"

        device.executeShellCommand(bleConnect)
        device.executeShellCommand(bleScan)
    }

    device.executeShellCommand(blePer)
    device.executeShellCommand(bleAdmin)
}

/**
 * Wraps starting the default activity, waiting for it to start and then allowing notifications in
 * one convenient call.
 */
fun MacrobenchmarkScope.startActivityAndGrantPermission() {
    startActivityAndWait()
    allowAllPermission()
}

/**
 * Waits for and returns the `primaryAppDrawer`
 */
fun MacrobenchmarkScope.getAppDrawer(): UiObject2 {
    device.wait(Until.hasObject(By.desc("primaryAppDrawerIcon")), 2_000)
    device.findObject(By.desc("primaryAppDrawerIcon")).click()
    return device.findObject(By.res("primaryAppDrawer"))
}

/**
 * Waits for and returns the `poposAppDrawer`
 */
fun MacrobenchmarkScope.getHomeAppDrawer(): UiObject2 {
    device.wait(Until.hasObject(By.res("drawerButton")), 2_000)
    device.findObject(By.res("drawerButton"))
    device.findObject(By.res("drawerButton")).click()
    return device.findObject(By.res("homeAppDrawer"))
}

/**
 * Waits for an object on the top app bar, passed in as [selector].
 */
fun MacrobenchmarkScope.waitForObjectHomeOnAppDrawer(selector: BySelector, timeout: Long = 2_000) {
    getHomeAppDrawer().wait(Until.hasObject(selector), timeout)
}
