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

import androidx.room.TypeConverter

class ListConverter {
    @TypeConverter
    fun fromList(list: List<Int>): String {
        val sb = StringBuilder()
        for (item in list) {
            sb.append(item).append(",")
        }
        return sb.toString()
    }

    @TypeConverter
    fun toList(string: String): List<Int> {
        val items = string.split(",")

        return items.map { it.trim() } // Trim to remove leading/trailing whitespace
            .filter { it.isNotEmpty() } // Filter out empty strings
            .map { it.toInt() }
    }

    @TypeConverter
    fun fromListString(list: List<String>): String {
        val sb = StringBuilder()
        for (item in list) {
            sb.append(item).append(",")
        }
        return sb.toString()
    }

    @TypeConverter
    fun toListString(string: String): List<String> {
        val items = string.split(",")

        return items.map { it.trim() } // Trim to remove leading/trailing whitespace
            .filter { it.isNotEmpty() } // Filter out empty strings
            .map { it }
    }
}
