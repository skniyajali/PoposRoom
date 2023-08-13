package com.niyaj.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class ProductWiseOrderDetailsDto(
    @Embedded
    val cartOrder: CartOrderEntity,

    @Relation(
        parentColumn = "addressId",
        entityColumn = "addressId",
        entity = AddressEntity::class,
        projection = ["addressName"]
    )
    val address: CustomerAddressName? = null,

    @Relation(
        parentColumn = "customerId",
        entityColumn = "customerId",
        entity = CustomerEntity::class,
        projection = ["customerPhone", "customerName"]
    )
    val customer: CustomerPhoneAndName? = null,

    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId",
        entity = CartEntity::class,
        projection = ["quantity"]
    )
    val productQuantity: ProductQuantity
)


data class ProductQuantity(val quantity: Int = 0)