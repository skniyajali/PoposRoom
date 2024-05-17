/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.database.util

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper

class DatabaseHelper: SupportSQLiteOpenHelper {
    override val databaseName: String?
        get() = TODO("Not yet implemented")

    override val readableDatabase: SupportSQLiteDatabase
        get() = TODO("Not yet implemented")

    override val writableDatabase: SupportSQLiteDatabase
        get() = TODO("Not yet implemented")

    override fun close() {
        TODO("Not yet implemented")
    }

    override fun setWriteAheadLoggingEnabled(enabled: Boolean) {
        TODO("Not yet implemented")
    }
}