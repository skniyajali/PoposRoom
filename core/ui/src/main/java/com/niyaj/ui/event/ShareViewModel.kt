package com.niyaj.ui.event

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.samples.apps.core.analytics.AnalyticsEvent
import com.samples.apps.core.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
open class ShareViewModel @Inject constructor(
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
    private val analyticsHelper: AnalyticsHelper,
): ViewModel() {

    private val _showDialog = MutableStateFlow(false)
    val showDialog = _showDialog.asStateFlow()

    open fun onDismissDialog() {
        viewModelScope.launch {
            _showDialog.value = false
        }
    }

    open fun onShowDialog() {
        viewModelScope.launch {
            _showDialog.value = true
        }
    }

    open fun shareContent(
        context: Context,
        title: String,
        uri: Uri,
    ) {
        viewModelScope.launch(ioDispatcher) {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, uri)
                setDataAndType(uri, "image/png")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val shareIntent = Intent.createChooser(sendIntent, title)
            ContextCompat.startActivity(context, shareIntent, null)
        }
    }

    open suspend fun saveImage(image: Bitmap, context: Context): Uri? {
        return withContext(ioDispatcher) {
            val imagesFolder = File(context.cacheDir, "images")
            try {
                imagesFolder.mkdirs()
                val file = File(imagesFolder, "shared_image.png")

                val stream = FileOutputStream(file)
                image.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.flush()
                stream.close()

                FileProvider.getUriForFile(context, "com.popos.fileprovider", file)
            } catch (e: IOException) {
                Log.d("saving bitmap", "saving bitmap error ${e.message}")
                null
            }
        }
    }
}

internal fun AnalyticsHelper.logProductDetailsViewed(productId: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "product_details_viewed",
            extras = listOf(
                AnalyticsEvent.Param("product_details_viewed", productId.toString()),
            ),
        ),
    )
}
