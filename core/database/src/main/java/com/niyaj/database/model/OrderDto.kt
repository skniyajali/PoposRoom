package com.niyaj.database.model

import androidx.room.Junction
import androidx.room.Relation
import androidx.room.RewriteQueriesToDropUnusedColumns
import com.niyaj.model.Order
import com.niyaj.model.OrderPrice
import com.niyaj.model.OrderType
import java.util.Date

@RewriteQueriesToDropUnusedColumns
data class OrderDto(
    val orderId: Int,
    val orderType: OrderType,
    val customerId: Int,
    val addressId: Int,

    @Relation(
        parentColumn = "customerId",
        entity = CustomerEntity::class,
        entityColumn = "customerId",
        projection = ["customerPhone", "customerName"],
    )
    val customer: CustomerPhoneAndName? = null,

    @Relation(
        parentColumn = "addressId",
        entity = AddressEntity::class,
        entityColumn = "addressId",
        projection = ["shortName"],
    )
    val address: AddressShortName? = null,

    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId",
        entity = CartPriceEntity::class,
        associateBy = Junction(CartPriceEntity::class),
    )
    val orderPrice: OrderPrice,

    val createdAt: Date,

    val updatedAt: Date? = null,
)

fun OrderDto.toExternalModel(): Order {
    return Order(
        orderId = orderId,
        orderType = orderType,
        customerPhone = customer?.customerPhone,
        customerAddress = address?.shortName,
        orderDate = updatedAt ?: createdAt,
        orderPrice = orderPrice,
    )
}