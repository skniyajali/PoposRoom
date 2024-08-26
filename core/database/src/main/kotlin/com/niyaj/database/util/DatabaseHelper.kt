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

package com.niyaj.database.util

import android.content.Context
import androidx.room.Room
import com.niyaj.database.PoposDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.use
import javax.inject.Inject

class DatabaseHelper @Inject constructor(
    private val context: Context,
) {
    fun isDatabaseValid(): Boolean {
        var newDatabase: PoposDatabase? = null
        try {
            newDatabase =
                Room.databaseBuilder(context, PoposDatabase::class.java, PoposDatabase.NAME).build()
            val database = newDatabase.openHelper.writableDatabase

            // Check if the database file exists and can be opened
            if (!database.isOpen) {
                return false
            }

            // Check if any tables exist in the database
            val cursor = database.query("SELECT name FROM sqlite_master WHERE type='table'")

            cursor.use {
                return it.count > 0
            }
        } catch (e: Exception) {
            return false
        } finally {
            newDatabase?.close()
        }
    }

    suspend fun deleteAllTables(): Boolean {
        var newDatabase: PoposDatabase? = null
        try {
            newDatabase =
                Room.databaseBuilder(context, PoposDatabase::class.java, PoposDatabase.NAME).build()

            withContext(Dispatchers.IO) {
                newDatabase.clearAllTables()
            }

            return true
        } catch (e: Exception) {
            return false
        } finally {
            newDatabase?.close()
        }
    }
}
