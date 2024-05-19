/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.feature.printer.bluetooth_printer.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.drawable.toBitmapOrNull
import java.io.File
import java.io.FileInputStream

object FileExtension {

    fun Context.getImageFromDeviceOrDefault(
        imageFileName: String,
    ): Bitmap? {
        return if (imageFileName.isEmpty()) {
            this.getDrawable(com.niyaj.core.ui.R.drawable.reslogo)?.toBitmapOrNull()
        } else {
            val file = File(this.filesDir, imageFileName)

            BitmapFactory.decodeStream(FileInputStream(file))
        }
    }
}