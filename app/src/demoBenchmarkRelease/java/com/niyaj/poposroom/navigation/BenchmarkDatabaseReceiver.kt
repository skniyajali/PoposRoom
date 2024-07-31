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

package com.niyaj.poposroom.navigation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.niyaj.common.result.RestoreBackupResult.DbError
import com.niyaj.common.result.RestoreBackupResult.FileError
import com.niyaj.common.result.RestoreBackupResult.Success
import com.niyaj.common.result.RestoreBackupResult.WrongFile
import com.niyaj.common.utils.showToast
import com.niyaj.data.repository.BackupRepository
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class BenchmarkDatabaseReceiver @Inject constructor(
    private val backupRepository: BackupRepository,
) : BroadcastReceiver() {
    companion object {
        const val ACTION_RESTORE_DATABASE = "com.niyaj.poposroom.demo.ACTION_RESTORE_DATABASE"
        const val FILE_NAME = "PoposBackup.zip"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("Received broadcast")

        if (intent.action == ACTION_RESTORE_DATABASE) {
            Timber.d("Received broadcast to restore database")
            restoreDatabase(context)
        } else {
            Timber.d("Unable to Received broadcast to restore database")
        }
    }

    private fun restoreDatabase(context: Context) {
        val file = copyAssetToInternalStorage(context)
        val fileUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )

        // Assuming your restore function is in a DatabaseManager class
        val result = backupRepository.restoreBackup(fileUri)
        when (result) {
            Success -> {
                context.showToast("Database restored successfully")
            }

            FileError -> {
                context.showToast("File error")
            }

            WrongFile -> {
                context.showToast("Wrong file")
            }

            DbError -> {
                context.showToast("Database error")
            }
        }
    }

    private fun copyAssetToInternalStorage(context: Context): File {
        val file = File(context.filesDir, FILE_NAME)

        if (!file.exists()) {
            context.assets.open(FILE_NAME).use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }

        return file
    }
}
