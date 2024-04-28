package com.niyaj.domain.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
    inline fun <reified T> writeData(context: Context, uri: Uri, data: List<T>): Boolean {
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
                Log.d("Exception", e.message.toString())
                Result.failure(e)
            } catch (e: IOException) {
                Log.d("Exception", e.message.toString())
                Result.failure(e)
            } catch (e: Exception) {
                Log.d("Exception", e.message.toString())
                Result.failure(e)
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend inline fun <reified T> writeDataFlow(
        context: Context,
        uri: Uri,
        data: List<T>,
    ): Flow<Result<Unit>> = callbackFlow {
        withContext(Dispatchers.IO) {
            try {
                val moshi = Moshi.Builder().build().adapter<List<T>>()
                val jsonData = moshi.toJson(data)

                val contentResolver = context.applicationContext.contentResolver
                val outputStream = contentResolver.openOutputStream(uri, "rwt")?.let { stream ->
                    stream.bufferedWriter().apply {
                        write(jsonData)
                        flush()
                        close()
                    }

                    awaitClose {
                        stream.close()
                    }

                    trySendBlocking(Result.success(Unit))
                }
            } catch (e: IOException) {
                trySendBlocking(Result.failure(e))
            } catch (e: Exception) {
                trySendBlocking(Result.failure(e))
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend inline fun <reified T> readData(context: Context, uri: Uri): List<T> {
        try {
            val container = mutableListOf<T>()

            val moshi = Moshi.Builder().build().adapter<List<T>>().nullSafe()

            withContext(Dispatchers.IO) {
                context.applicationContext.contentResolver.openInputStream(uri)?.use {
                    it.bufferedReader().use { reader ->
                        delay(50L)

                        moshi.fromJson(reader.readText())?.let { it1 -> container.addAll(it1) }
                    }

                    delay(50L)

                    it.close()
                }
            }

            return container.toList()
        } catch (e: FileNotFoundException) {
            return emptyList()
        } catch (e: IOException) {
            return emptyList()
        } catch (e: Exception) {
            return emptyList()
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
            } catch (e: Exception) {
                emptyList()
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
