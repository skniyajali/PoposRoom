package com.niyaj.database.model

import androidx.room.Relation

data class AddressWiseOrderDto(
    val orderId: Int,

    val createdAt: Long,

    val updatedAt: Long? = null,

    val customerId: Int,

    @Relation(
        parentColumn = "customerId",
        entityColumn = "customerId",
        entity = CustomerEntity::class,
        projection = ["customerPhone", "customerName"]
    )
    val customer: CustomerPhoneAndName,

    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId",
        entity = CartPriceEntity::class,
        projection = ["totalPrice"]
    )
    val orderPrice: OrderTotalPrice,
)

data class CustomerPhoneAndName(
    val customerPhone: String,
    val customerName: String? = null,
)