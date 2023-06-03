package com.niyaj.poposroom.features.common.database.utils

import androidx.room.TypeConverter
import java.util.Date

class TimestampConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}