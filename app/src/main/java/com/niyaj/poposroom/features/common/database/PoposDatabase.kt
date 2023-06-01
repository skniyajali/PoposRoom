package com.niyaj.poposroom.features.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.niyaj.poposroom.features.addon_item.dao.AddOnItemDao
import com.niyaj.poposroom.features.addon_item.domain.model.AddOnItem
import com.niyaj.poposroom.features.address.dao.AddressDao
import com.niyaj.poposroom.features.address.domain.model.Address
import com.niyaj.poposroom.features.category.dao.CategoryDao
import com.niyaj.poposroom.features.category.domain.model.Category
import com.niyaj.poposroom.features.charges.dao.ChargesDao
import com.niyaj.poposroom.features.charges.domain.model.Charges
import com.niyaj.poposroom.features.common.utils.TimestampConverters
import com.niyaj.poposroom.features.customer.dao.CustomerDao
import com.niyaj.poposroom.features.customer.domain.model.Customer

@Database(
    entities = [
        AddOnItem::class,
        Address::class,
        Charges::class,
        Category::class,
        Customer::class,
    ],
    version = 3,
    autoMigrations = [],
    exportSchema = true,
)
@TypeConverters(TimestampConverters::class)
abstract class PoposDatabase : RoomDatabase() {
    abstract fun addOnItemDao(): AddOnItemDao
    abstract fun addressDao(): AddressDao
    abstract fun chargesDao(): ChargesDao
    abstract fun categoryDao(): CategoryDao
    abstract fun customerDao(): CustomerDao
}