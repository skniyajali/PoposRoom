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
}