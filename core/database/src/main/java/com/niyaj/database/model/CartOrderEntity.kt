package com.niyaj.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niyaj.model.OrderStatus
import com.niyaj.model.OrderType
import java.util.Date


@Entity(tableName = "cartorder")
data class CartOrderEntity(
    @PrimaryKey(autoGenerate = true)
    val orderId: Int,

    val orderType: OrderType,

    val orderStatus: OrderStatus,

    val doesChargesIncluded: Boolean,

    @ColumnInfo(index = true)
    val addressId: Int,

    @ColumnInfo(index = true)
    val customerId: Int,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date = Date(),

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null,
)