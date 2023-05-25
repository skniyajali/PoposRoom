package com.niyaj.poposroom.features.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.niyaj.poposroom.features.addon_item.dao.AddOnItemDao
import com.niyaj.poposroom.features.addon_item.domain.model.AddOnItem

@Database(
    entities = [
        AddOnItem::class
    ],
    version = 1,
    autoMigrations = [],
    exportSchema = true,
)
abstract class PoposDatabase : RoomDatabase() {
    abstract fun addOnItemDao(): AddOnItemDao
}