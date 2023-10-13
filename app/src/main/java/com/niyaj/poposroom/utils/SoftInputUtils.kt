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
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
    }
}
