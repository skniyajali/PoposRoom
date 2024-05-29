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

package com.niyaj.poposroom.utils

import android.app.Activity
import android.os.Build
import android.view.WindowInsets
import android.view.WindowManager

fun Activity.enableSystemInsetsHandling() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val currentInsetTypes = mutableSetOf<Int>()

        currentInsetTypes.add(WindowInsets.Type.systemBars())
        currentInsetTypes.add(WindowInsets.Type.statusBars())
        currentInsetTypes.add(WindowInsets.Type.mandatorySystemGestures())
        currentInsetTypes.add(WindowInsets.Type.ime())

        window.setDecorFitsSystemWindows(false)

        window.decorView.setOnApplyWindowInsetsListener { v, _ ->
            val currentInsetTypeMask = currentInsetTypes.fold(0) { accumulator, type ->
                accumulator or type
            }
            val insets = window.decorView.rootWindowInsets.getInsets(currentInsetTypeMask)
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom)

            WindowInsets.Builder()
                .setInsets(currentInsetTypeMask, insets)
                .build()
        }
    } else {
        @Suppress("DEPRECATION")
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE,
        )
    }
}
