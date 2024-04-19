package com.niyaj.database.model

import androidx.room.Relation
import com.niyaj.model.DeliveryReport
import java.util.Date

data class DeliveryReportDto(
    val orderId: Int,

    val createdAt: Date,

    val updatedAt: Date? = null,

    val addressId: Int,

    @Relation(
        parentColumn = "addressId",
        entityColumn = "addressId",
        entity = AddressEntity::class,
        projection = ["shortName"]
    )
    val address: AddressShortName,

    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId",
        entity = CartPriceEntity::class,
        projection = ["totalPrice"]
    )
    val orderPrice: OrderTotalPrice,
)


data class AddressShortName(
    val shortName: String
)

fun DeliveryReportDto.toExternalModel() = DeliveryReport(
    orderId = orderId,
    createdAt = createdAt,
    updatedAt = updatedAt,
    addressName = address.shortName,
    orderPrice = orderPrice.totalPrice
)