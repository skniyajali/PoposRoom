package com.niyaj.print.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.core.graphics.drawable.toBitmapOrNull
import com.niyaj.feature.print_order.R
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream

object FileExtension {

    private val cache = LruCache<String, Bitmap>(100)

    suspend fun Context.getImageFromDeviceOrDefault(
        imageFileName: String,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Bitmap? {
        return withContext(ioDispatcher) {
            if (imageFileName.isEmpty()) {
                this@getImageFromDeviceOrDefault.getDrawable(R.drawable.reslogo)?.toBitmapOrNull()
            } else {
                val file = File(this@getImageFromDeviceOrDefault.filesDir, imageFileName)

                BitmapFactory.decodeStream(FileInputStream(file))
            }
        }
    }
}