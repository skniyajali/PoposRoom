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

package com.niyaj.domain.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private const val JSON_FILE_TYPE = "application/json"
private const val JSON_FILE_EXTENSION = ".json"
private const val SAVABLE_FILE_NAME = "popos"

object ImportExport {

    fun openFile(
        context: Context,
        pickerInitialUri: Uri = getUri(context),
        mimeType: String = JSON_FILE_TYPE,
    ): Intent {
        val intent = Intent(
            Intent.ACTION_OPEN_DOCUMENT,
            pickerInitialUri,
        ).apply {
            type = mimeType
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        return intent
    }

    fun createFile(context: Context, fileName: String = SAVABLE_FILE_NAME): Intent {
        val intent = Intent(
            Intent.ACTION_CREATE_DOCUMENT,
            getUri(context),
        ).apply {
            type = JSON_FILE_TYPE
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.EXTRA_TITLE, fileName.plus(JSON_FILE_EXTENSION))
        }

        return intent
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend inline fun <reified T> writeDataAsync(
        context: Context,
        uri: Uri,
        data: List<T>,
    ): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val moshi = Moshi.Builder().build().adapter<List<T>>()
                val jsonData = moshi.toJson(data)
                val lock = ReentrantLock()

                context.applicationContext.contentResolver.openOutputStream(uri, "rwt")
                    ?.use { outputStream ->
                        val bufferedWriter = outputStream.bufferedWriter()

                        lock.withLock {
                            bufferedWriter.write(jsonData)
                            bufferedWriter.flush()
                        }

                        outputStream.close()
                    }

                Result.success("Data written successfully")
            } catch (e: FileNotFoundException) {
                Result.failure(e)
            } catch (e: IOException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend inline fun <reified T> readDataAsync(context: Context, uri: Uri): List<T> {
        return withContext(Dispatchers.IO) {
            try {
                val container = mutableListOf<T>()
                val moshi = Moshi.Builder().build().adapter<List<T>>().nullSafe()

                context.applicationContext.contentResolver.openInputStream(uri)
                    ?.use { inputStream ->
                        val reader = inputStream.bufferedReader()
                        val jsonData = async { reader.readText() }
                        jsonData.await().let { data ->
                            moshi.fromJson(data)?.let { container.addAll(it) }
                        }
                        reader.close()
                    }

                container.toList()
            } catch (e: FileNotFoundException) {
                emptyList()
            } catch (e: IOException) {
                emptyList()
            } catch (e: JsonDataException) {
                emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend inline fun <reified T> readFile(context: Context, uri: Uri): Result<List<T>> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val moshi = Moshi.Builder().build().adapter<List<T>>()
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val jsonData = inputStream.bufferedReader().readText()
                    moshi.fromJson(jsonData) ?: emptyList()
                } ?: emptyList()
            }
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
