package com.niyaj.poposroom.features.cart_order.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.niyaj.poposroom.features.address.domain.model.Address
import com.niyaj.poposroom.features.cart_order.domain.utils.OrderStatus
import com.niyaj.poposroom.features.cart_order.domain.utils.OrderType
import com.niyaj.poposroom.features.common.utils.toTime
import com.niyaj.poposroom.features.customer.domain.model.Customer
import java.util.Date


@Entity(tableName = "cartorder")
data class CartOrderEntity(
    @PrimaryKey(autoGenerate = true)
    val orderId: Int,

    val orderType: OrderType = OrderType.DineIn,

    val orderStatus: OrderStatus = OrderStatus.PROCESSING,

    val doesChargesIncluded: Boolean = false,

    @ColumnInfo(index = true)
    val addressId: Int,

    @ColumnInfo(index = true)
    val customerId: Int,

    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date = Date(),

    @ColumnInfo(defaultValue = "NULL")
    val updatedAt: Date? = null,
)


data class CartOrder(
    val orderId: Int = 0,

    val orderType: OrderType = OrderType.DineIn,

    val orderStatus: OrderStatus = OrderStatus.PROCESSING,

    val doesChargesIncluded: Boolean = false,

    val customer: Customer = Customer(),

    val address: Address = Address(),

    val createdAt: Date = Date(),

    val updatedAt: Date? = null,
)


fun List<CartOrder>.filterCartOrder(searchText: String): List<CartOrder> {
    return if (searchText.isNotEmpty()){
        this.filter {cartOrder ->
            cartOrder.orderStatus.name.contains(searchText, true) ||
            cartOrder.customer.customerPhone.contains(searchText, true) ||
            cartOrder.customer.customerName?.contains(searchText, true) == true ||
            cartOrder.address.addressName.contains(searchText, true) ||
            cartOrder.address.shortName.contains(searchText, true) ||
            cartOrder.orderType.name.contains(searchText, true) ||
            cartOrder.orderId.toString().contains(searchText, true) ||
            cartOrder.createdAt.toTime.contains(searchText, true) ||
            cartOrder.updatedAt?.toTime?.contains(searchText, true) == true
        }
    }else this
}