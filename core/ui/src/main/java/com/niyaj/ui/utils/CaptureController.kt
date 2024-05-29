/*
 * Copyright 2022 Sk Niyaj Ali
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Controller for capturing [Composable] content.
 * @see com.niyaj.ui.utils.Capturable for implementation details.
 */
class CaptureController internal constructor() {

    /**
     * Medium for providing capture requests
     */
    private val _captureRequests = MutableSharedFlow<CaptureRequest>(extraBufferCapacity = 1)
    internal val captureRequests = _captureRequests.asSharedFlow()

    var readyForCapture by mutableStateOf(false)
        private set

    /**
     * Creates and send a Bitmap capture request
     *
     * Make sure to call this method as a part of callback function and not as a part of the
     * [Composable] function itself.
     *
     */
    fun captureLongScreenshot() {
        readyForCapture = true
    }

    /**
     * Creates and requests for a Bitmap capture with specified [config] and returns
     * an [ImageBitmap] asynchronously.
     *
     * This method is safe to be called from the "main" thread directly.
     *
     * Make sure to call this method as a part of callback function and not as a part of the
     * [Composable] function itself.
     *
     * @param config Bitmap config of the desired bitmap. Defaults to [Bitmap.Config.ARGB_8888]
     */
    @ExperimentalComposeApi
    fun captureAsync(config: Bitmap.Config = Bitmap.Config.ARGB_8888): Deferred<ImageBitmap> {
        val deferredImageBitmap = CompletableDeferred<ImageBitmap>()
        return deferredImageBitmap.also {
            _captureRequests.tryEmit(CaptureRequest(imageBitmapDeferred = it, config = config))
        }
    }

    internal fun captured() {
        readyForCapture = false
    }

    /**
     * Holds information of capture request
     */
    internal class CaptureRequest(
        val imageBitmapDeferred: CompletableDeferred<ImageBitmap>,
        val config: Bitmap.Config,
    )
}

/**
 * Creates [CaptureController] and remembers it.
 */
@Composable
fun rememberCaptureController(): CaptureController {
    return remember { CaptureController() }
}
