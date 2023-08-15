package com.niyaj.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileInputStream

class ImageStorageManager {
    companion object {
        fun saveToInternalStorage(context: Context, bitmapImage: Bitmap?, imageFileName: String): Boolean {
            bitmapImage?.let {
                deleteImageFromInternalStorage(context, imageFileName)

                return context.openFileOutput(imageFileName, Context.MODE_PRIVATE).use { fos ->
                    bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
                }
            }

            return false
        }

        fun getImageFromInternalStorage(context: Context, imageFileName: String): Bitmap? {
            val directory = context.filesDir
            val file = File(directory, imageFileName)
            return BitmapFactory.decodeStream(FileInputStream(file))
        }

        private fun deleteImageFromInternalStorage(context: Context, imageFileName: String): Boolean {
            val dir = context.filesDir
            val file = File(dir, imageFileName)
            return file.delete()
        }
    }
}