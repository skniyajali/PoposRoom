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

package com.niyaj.ui.utils

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix

/**
 * Helper class for encoding barcodes as a Bitmap.
 *
 * Adapted from QRCodeEncoder, from the zxing project:
 * https://github.com/zxing/zxing
 *
 * Licensed under the Apache License, Version 2.0.
 */
class QRCodeEncoder {

    private fun createBitmap(matrix: BitMatrix): Bitmap {
        val width = matrix.width
        val height = matrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (matrix[x, y]) BLACK else WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    @Throws(WriterException::class)
    fun encode(contents: String?, format: BarcodeFormat?, width: Int, height: Int): BitMatrix {
        return try {
            MultiFormatWriter().encode(contents, format, width, height)
        } catch (e: WriterException) {
            throw e
        } catch (e: Exception) {
            // ZXing sometimes throws an IllegalArgumentException
            throw WriterException(e)
        }
    }

    @Throws(WriterException::class)
    fun encode(
        contents: String?,
        format: BarcodeFormat?,
        width: Int,
        height: Int,
        hints: Map<EncodeHintType?, *>?,
    ): BitMatrix {
        return try {
            MultiFormatWriter().encode(contents, format, width, height, hints)
        } catch (e: WriterException) {
            throw e
        } catch (e: Exception) {
            throw WriterException(e)
        }
    }

    @Throws(WriterException::class)
    fun encodeBitmap(
        contents: String,
        format: BarcodeFormat = DEFAULT_BARCODE_FORMAT,
        width: Int = WIDTH,
        height: Int = HEIGHT,
    ): Bitmap {
        return createBitmap(encode(contents, format, width, height))
    }

    @Throws(WriterException::class)
    fun encodeBitmap(
        contents: String?,
        format: BarcodeFormat?,
        width: Int,
        height: Int,
        hints: Map<EncodeHintType?, *>?,
    ): Bitmap {
        return createBitmap(encode(contents, format, width, height, hints))
    }

    companion object {
        private const val WHITE = -0x1
        private const val BLACK = -0x1000000
        private const val WIDTH = 512
        private const val HEIGHT = 512
        private val DEFAULT_BARCODE_FORMAT = BarcodeFormat.QR_CODE
    }
}
