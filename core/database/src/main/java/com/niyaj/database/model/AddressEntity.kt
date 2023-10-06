package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niyaj.model.Address
import java.util.Date

@Entity(tableName = "address")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    val addressId: Int = 0,

    val addressName: String = "",

    val shortName: String = "",

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date = Date(),

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null,
)


fun AddressEntity.asExternalModel(): Address {
    return Address(
        addressId = this.addressId,
        addressName = this.addressName,
        shortName = this.shortName,
        createdAt = this.createdAt.time,
        updatedAt = this.updatedAt?.time
    )
}