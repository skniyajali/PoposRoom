package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.AddOnItem
import com.niyaj.model.Address
import com.niyaj.model.CartOrder
import com.niyaj.model.CartOrderWithAddOnAndCharges
import com.niyaj.model.Charges
import com.niyaj.model.Customer
import com.niyaj.model.Selected
import kotlinx.coroutines.flow.Flow

interface CartOrderRepository {

    suspend fun getAllProcessingCartOrders(): Flow<List<CartOrder>>

    fun getSelectedCartOrder(): Flow<Selected?>

    suspend fun getAllAddOnItem(): Flow<List<AddOnItem>>

    suspend fun getAllCharges(): Flow<List<Charges>>

    suspend fun insertOrUpdateSelectedOrder(selected: Selected): Resource<Boolean>

    suspend fun getAllAddresses(searchText: String): Flow<List<Address>>

    suspend fun getAllCustomer(searchText: String): Flow<List<Customer>>

    suspend fun getAllCartOrders(searchText: String, viewAll: Boolean): Flow<List<CartOrder>>

    suspend fun getCartOrderById(orderId: Int): Resource<CartOrderWithAddOnAndCharges?>

    suspend fun getLastCreatedOrderId(orderId: Int): Int

    suspend fun addOrIgnoreAddress(newAddress: Address): Int

    suspend fun addOrIgnoreCustomer(newCustomer: Customer): Int

    suspend fun createOrUpdateCartOrder(newCartOrder: CartOrderWithAddOnAndCharges): Resource<Boolean>

    suspend fun deleteCartOrder(orderId: Int): Resource<Boolean>

    suspend fun deleteCartOrders(orderIds: List<Int>): Resource<Boolean>
}