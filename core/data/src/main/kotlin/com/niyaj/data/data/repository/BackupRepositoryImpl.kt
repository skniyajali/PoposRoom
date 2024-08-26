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

package com.niyaj.data.data.repository

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.niyaj.common.result.CreateBackupResult
import com.niyaj.common.result.RestoreBackupResult
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.BackupRepository
import com.niyaj.data.utils.logBackupPerformed
import com.niyaj.data.utils.logBackupRestorePerformed
import com.niyaj.database.PoposDatabase
import com.niyaj.database.util.DatabaseHelper
import okio.use
import timber.log.Timber
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import javax.inject.Inject

class BackupRepositoryImpl @Inject constructor(
    private val context: Context,
    private val databaseHelper: DatabaseHelper,
    private val analyticsHelper: AnalyticsHelper,
) : BackupRepository {

    companion object {
        private const val ZIP_IMAGES_DIR = "images/"
        private const val ZIP_DATABASE_DIR = "database/"
    }

    private val appImagesDir = File(context.filesDir, "images")
    private val dbCacheBackupDir = File(context.cacheDir, "db_backup_files").path

    override fun createBackup(): CreateBackupResult {
        val zipFile = generateDatabaseZip()
        if (!hasValidDatabaseFiles(zipFile)) {
            zipFile.delete()
            return CreateBackupResult.Error
        }

        val backupUri =
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", zipFile)
        analyticsHelper.logBackupPerformed()
        return CreateBackupResult.Success(backupUri)
    }

    override fun restoreBackup(backupUri: Uri): RestoreBackupResult {
        val cachedZip = cacheZipFile(backupUri)

        if (cachedZip == null || !cachedZip.exists()) {
            return RestoreBackupResult.FileError
        }

        if (!hasValidDatabaseFiles(cachedZip)) {
            cachedZip.delete()
            return RestoreBackupResult.WrongFile
        }

        val dbFile = context.getDatabasePath(PoposDatabase.NAME)
        dbFile.mkdirsIfNeed()

        val dbFolder = dbFile.path.substringBeforeLast(File.separator)
        val dbFilesPaths = PoposDatabase.getFilePaths(context)

        backupCurrentDb(dbFolder, dbFilesPaths)
        extractDBFromZip(cachedZip, dbFolder)

        return if (databaseHelper.isDatabaseValid()) {
            clearImages()
            extractImagesFromZip(cachedZip)
            deleteBackupDb()
            cachedZip.delete()
            analyticsHelper.logBackupRestorePerformed()
            RestoreBackupResult.Success
        } else {
            deleteDbFiles(dbFilesPaths)
            restoreBackupDb(dbFolder, dbFilesPaths)
            cachedZip.delete()
            RestoreBackupResult.DbError
        }
    }

    private fun generateDatabaseZip(): File {
        val formatter = SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault())
        val fileName = "PoposRoom_backup_${formatter.format(Date())}"
        val zipFile = File(context.externalCacheDir, "$fileName.zip")

        try {
            ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { output ->
                putDatabaseToZip(output)
                putImagesToZip(output)
            }
        } catch (e: Exception) {
            Timber.e(e)
        }

        return zipFile
    }

    private fun putImagesToZip(zipOutput: ZipOutputStream) {
        if (appImagesDir.exists() && appImagesDir.isDirectory) {
            appImagesDir.listFiles()?.forEach { imageFile ->
                copyFileToZip(imageFile, ZIP_IMAGES_DIR.plus(imageFile.name), zipOutput)
            }
        }
    }

    private fun putDatabaseToZip(zipOutput: ZipOutputStream) {
        PoposDatabase.getFilePaths(context).forEach { filePath ->
            val file = File(filePath)
            copyFileToZip(file, ZIP_DATABASE_DIR.plus(file.name), zipOutput)
        }
    }

    private fun copyFileToZip(file: File, name: String, zipOutput: ZipOutputStream) {
        FileInputStream(file).use { input ->
            val entry = ZipEntry(name)
            zipOutput.putNextEntry(entry)
            input.copyTo(zipOutput)
            zipOutput.closeEntry()
        }
    }

    private fun backupCurrentDb(dbDir: String, dbFilePaths: List<String>) {
        dbFilePaths.forEach { path ->
            moveFile(dbDir, getFileName(path), dbCacheBackupDir)
        }
    }

    private fun deleteDbFiles(dbFilePaths: List<String>) {
        dbFilePaths.forEach { path ->
            File(path).delete()
        }
    }

    private fun deleteBackupDb() {
        File(dbCacheBackupDir).deleteRecursively()
    }

    private fun restoreBackupDb(dbDir: String, dbFilePaths: List<String>) {
        dbFilePaths.forEach { path ->
            moveFile(dbCacheBackupDir, getFileName(path), dbDir)
        }
    }

    private fun moveFile(inputPath: String, fileName: String, outputPath: String) {
        try {
            File(outputPath).mkdirsIfNeed()
            File(inputPath).mkdirsIfNeed()
            FileInputStream("$inputPath/$fileName").use { input ->
                FileOutputStream("$outputPath/$fileName").use { output ->
                    input.copyTo(output)
                }
            }
            File("$inputPath/$fileName").delete()
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun extractDBFromZip(zipFile: File, destDirectory: String) {
        try {
            ZipFile(zipFile).use { zip ->
                zip.forEachEntry { entry ->
                    if (entry.name.startsWith(ZIP_DATABASE_DIR)) {
                        zip.extractEntryTo(entry, destDirectory)
                    }
                }
            }
        } catch (e: IOException) {
            Timber.e(e)
        }
    }

    private fun extractImagesFromZip(zipFile: File) {
        appImagesDir.mkdirsIfNeed()
        try {
            ZipFile(zipFile).use { zip ->
                zip.forEachEntry { entry ->
                    if (entry.name.startsWith(ZIP_IMAGES_DIR)) {
                        zip.extractEntryTo(entry, appImagesDir.path)
                    }
                }
            }
        } catch (e: IOException) {
            Timber.e(e)
        }
    }

    private fun ZipFile.forEachEntry(action: (ZipEntry) -> Unit) {
        this.entries().asSequence().forEach(action)
    }

    private fun ZipFile.extractEntryTo(entry: ZipEntry, destinationDir: String) {
        getInputStream(entry).use { input ->
            val file = File(destinationDir, entry.name.substringAfterLast(File.separator))
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun clearImages() {
        appImagesDir.deleteRecursively()
    }

    private fun cacheZipFile(uri: Uri): File? {
        return try {
            val destinationFile = File(context.cacheDir, getFileName(uri))
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }
            destinationFile
        } catch (e: IOException) {
            Timber.e(e)
            null
        }
    }

    private fun hasValidDatabaseFiles(zipFile: File): Boolean {
        PoposDatabase.dbFileNames.forEach { fileName ->
            val zipDBFileName = ZIP_DATABASE_DIR + fileName
            if (!zipContains(zipFile, zipDBFileName)) return false
        }
        return true
    }

    private fun zipContains(zipFile: File, fileName: String): Boolean {
        try {
            ZipFile(zipFile).use { zip ->
                zip.entries().asSequence().forEach { entry ->
                    if (entry.name == fileName) return true
                }
            }
            return false
        } catch (e: Exception) {
            Timber.e(e)
            return false
        }
    }

    private fun File.mkdirsIfNeed() {
        if (!exists()) {
            mkdirs()
        }
    }

    private fun getFileName(path: String): String {
        return File(path).name
    }

    private fun getFileName(uri: Uri): String {
        return uri.path?.let { path -> File(path).name } ?: ""
    }
}
