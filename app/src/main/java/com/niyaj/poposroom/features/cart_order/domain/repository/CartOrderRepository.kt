package com.niyaj.poposroom.features.cart_order.domain.repository

import com.niyaj.poposroom.features.address.domain.model.Address
import com.niyaj.poposroom.features.cart_order.domain.model.CartOrder
import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.customer.domain.model.Customer
import com.niyaj.poposroom.features.selected.domain.model.Selected
import kotlinx.coroutines.flow.Flow

interface CartOrderRepository {

    suspend fun getAllProcessingCartOrders(): Flow<List<CartOrder>>

    fun getSelectedCartOrder(): Flow<Selected?>

    suspend fun insertOrUpdateSelectedOrder(selected: Selected): Resource<Boolean>

    suspend fun getAllAddresses(searchText: String): Flow<List<Address>>

    suspend fun getAllCustomer(searchText: String): Flow<List<Customer>>

    suspend fun getAllCartOrders(searchText: String): Flow<List<CartOrder>>

    suspend fun getCartOrderById(orderId: Int): Resource<CartOrder?>

    suspend fun getLastCreatedOrderId(orderId: Int): Int

    suspend fun addOrIgnoreAddress(newAddress: Address): Int

    suspend fun addOrIgnoreCustomer(newCustomer: Customer): Int

    suspend fun createOrUpdateCartOrder(newCartOrder: CartOrder): Resource<Boolean>

    suspend fun deleteCartOrder(orderId: Int): Resource<Boolean>

    suspend fun deleteCartOrders(orderIds: List<Int>): Resource<Boolean>
}