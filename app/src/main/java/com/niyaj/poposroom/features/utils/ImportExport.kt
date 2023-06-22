package com.niyaj.poposroom.features.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.niyaj.poposroom.features.common.utils.Constants.JSON_FILE_EXTENSION
import com.niyaj.poposroom.features.common.utils.Constants.JSON_FILE_TYPE
import com.niyaj.poposroom.features.common.utils.Constants.SAVABLE_FILE_NAME
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import kotlinx.coroutines.delay
import timber.log.Timber
import java.io.FileNotFoundException
import java.io.IOException

object ImportExport {

    @RequiresApi(Build.VERSION_CODES.Q)
    internal fun openFile(
        context: Context,
        pickerInitialUri: Uri = getUri(context),
    ): Intent {
        val intent = Intent(
            Intent.ACTION_OPEN_DOCUMENT,
            pickerInitialUri
        ).apply {
            type = JSON_FILE_TYPE
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        return intent
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    internal fun createFile(context: Context, fileName: String = SAVABLE_FILE_NAME): Intent {
        val intent = Intent(
            Intent.ACTION_CREATE_DOCUMENT,
            getUri(context)
        ).apply {
            type = JSON_FILE_TYPE
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_TITLE, fileName.plus(JSON_FILE_EXTENSION))
        }

        return intent
    }

    @OptIn(ExperimentalStdlibApi::class)
    internal inline fun <reified T> writeData(context: Context, uri: Uri, data: List<T>): Boolean {
        try {

            val moshi = Moshi.Builder().build().adapter<List<T>>()
            val list = moshi.toJson(data)

            context.applicationContext.contentResolver.openOutputStream(uri, "rwt")?.use {
                it.flush()
                it.bufferedWriter().use { writer ->
                    writer.write(list)
                }
            }

            return true

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return false
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }


    @OptIn(ExperimentalStdlibApi::class)
    internal suspend inline fun <reified T> readData(context: Context, uri: Uri): List<T> {
        try {
            val container = mutableListOf<T>()

            val moshi = Moshi.Builder().build().adapter<List<T>>().nullSafe()

            context.applicationContext.contentResolver.openInputStream(uri)?.use {
                it.bufferedReader().use { reader ->
                    delay(50L)

                    moshi.fromJson(reader.readText())?.let { it1 -> container.addAll(it1) }
                }

                delay(50L)

                it.close()
            }

            return container.toList()
        } catch (e: FileNotFoundException) {
            Timber.e(e)
            return emptyList()
        } catch (e: IOException) {
            Timber.e(e)
            return emptyList()
        } catch (e: Exception) {
            Timber.e(e)
            return emptyList()
        }
    }

    private fun getUri(context: Context): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Downloads.INTERNAL_CONTENT_URI
        } else {
            getUriBelowQ(context)
        }
    }

    private fun getUriBelowQ(context: Context): Uri {
        val result = context.filesDir

        return Uri.fromFile(result)
    }
}
