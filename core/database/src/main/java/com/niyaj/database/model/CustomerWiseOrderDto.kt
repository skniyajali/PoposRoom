package com.niyaj.database.model

import androidx.room.Relation

data class CustomerWiseOrderDto(
    val orderId: Int,

    val createdAt: Long,

    val updatedAt: Long? = null,

    val addressId: Int,

    @Relation(
        parentColumn = "addressId",
        entityColumn = "addressId",
        entity = AddressEntity::class,
        projection = ["addressName"]
    )
    val address: CustomerAddressName,

    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId",
        entity = CartPriceEntity::class,
        projection = ["totalPrice"]
    )
    val orderPrice: OrderTotalPrice,
)

data class CustomerAddressName(
    val addressName: String,
)