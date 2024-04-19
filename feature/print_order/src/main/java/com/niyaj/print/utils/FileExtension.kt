package com.niyaj.print.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.drawable.toBitmapOrNull
import com.niyaj.feature.print_order.R
import java.io.File
import java.io.FileInputStream

object FileExtension {

    fun Context.getImageFromDeviceOrDefault(
        imageFileName: String
    ): Bitmap? {
        return if (imageFileName.isEmpty()) {
            this.getDrawable(R.drawable.reslogo)?.toBitmapOrNull()
        } else {
            val file = File(this.filesDir, imageFileName)

            BitmapFactory.decodeStream(FileInputStream(file))
        }
    }
}