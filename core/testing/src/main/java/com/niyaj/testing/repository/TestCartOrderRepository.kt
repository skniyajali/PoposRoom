/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.testing.repository

import com.niyaj.common.result.Resource
import com.niyaj.data.repository.CartOrderRepository
import com.niyaj.model.AddOnItem
import com.niyaj.model.Address
import com.niyaj.model.CartOrder
import com.niyaj.model.CartOrderWithAddOnAndCharges
import com.niyaj.model.Charges
import com.niyaj.model.Customer
import com.niyaj.model.EmployeeNameAndId
import com.niyaj.model.OrderStatus.PROCESSING
import com.niyaj.model.Selected
import com.niyaj.model.filterCartOrder
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

class TestCartOrderRepository : CartOrderRepository {

    /**
     * The backing address list for testing
     */
    private val items = MutableStateFlow(mutableListOf<CartOrder>())
    private val addOnItems = MutableStateFlow(mutableListOf<Int>())
    private val charges = MutableStateFlow(mutableListOf<Int>())
    private val address = MutableStateFlow(Address())
    private val customer = MutableStateFlow(Customer())

    private val selectedItem = MutableStateFlow<Selected?>(null)

    override suspend fun getAllProcessingCartOrders(): Flow<List<CartOrder>> {
        return items.mapLatest { orders ->
            orders.filter { it.orderStatus == PROCESSING }
        }
    }

    override fun getSelectedCartOrder(): Flow<Selected?> = selectedItem

    override suspend fun getAllAddOnItem(): Flow<List<AddOnItem>> {
        return flowOf(emptyList())
    }

    override suspend fun getAllCharges(): Flow<List<Charges>> {
        return flowOf(emptyList())
    }

    override suspend fun getDeliveryPartners(): Flow<List<EmployeeNameAndId>> {
        return flowOf(emptyList())
    }

    override suspend fun insertOrUpdateSelectedOrder(selected: Selected): Resource<Boolean> {
        selectedItem.update { selected }
        return Resource.Success(true)
    }

    override suspend fun getAllAddresses(searchText: String): Flow<List<Address>> {
        return flowOf(emptyList())
    }

    override suspend fun getAllCustomer(searchText: String): Flow<List<Customer>> {
        return flowOf(emptyList())
    }

    override suspend fun getAllCartOrders(
        searchText: String,
        viewAll: Boolean,
    ): Flow<List<CartOrder>> {
        return items.mapLatest { list ->
            list.filter { if (!viewAll) it.orderStatus == PROCESSING else true }
                .filterCartOrder(searchText)
        }
    }

    override suspend fun getCartOrderById(
        orderId: Int,
    ): Resource<CartOrderWithAddOnAndCharges?> {
        return items.value.find { it.orderId == orderId }?.let {
            Resource.Success(
                CartOrderWithAddOnAndCharges(
                    cartOrder = it,
                    addOnItems = addOnItems.value.toImmutableList(),
                    charges = charges.value.toImmutableList(),
                ),
            )
        } ?: Resource.Success(null)
    }

    override suspend fun getLastCreatedOrderId(orderId: Int): Int {
        return items.value.lastOrNull()?.orderId?.plus(1) ?: 1
    }

    override suspend fun addOrIgnoreAddress(newAddress: Address): Int {
        return address.updateAndGet { newAddress }.addressId
    }

    override suspend fun addOrIgnoreCustomer(newCustomer: Customer): Int {
        return customer.updateAndGet { newCustomer }.customerId
    }

    override suspend fun createOrUpdateCartOrder(
        newCartOrder: CartOrderWithAddOnAndCharges,
    ): Resource<Boolean> {
        val cartOrder = newCartOrder.cartOrder
        val newAddOns = newCartOrder.addOnItems
        val newCharges = newCartOrder.charges

        addOrIgnoreAddress(cartOrder.address)
        addOrIgnoreCustomer(cartOrder.customer)

        newAddOns.forEach { addOnItems.value.add(it) }
        newCharges.forEach { charges.value.add(it) }

        selectedItem.update { Selected(orderId = cartOrder.orderId) }

        return Resource.Success(items.value.add(cartOrder))
    }

    override suspend fun deleteCartOrder(orderId: Int): Resource<Boolean> {
        return Resource.Success(items.value.removeIf { it.orderId == orderId })
    }

    override suspend fun deleteCartOrders(orderIds: List<Int>): Resource<Boolean> {
        return Resource.Success(items.value.removeIf { it.orderId in orderIds })
    }

    override suspend fun getCartOrderIdsByAddressId(addressId: Int): List<Int> {
        return emptyList()
    }

    override suspend fun getCartOrderIdsByCustomerId(customerId: Int): List<Int> {
        return emptyList()
    }
}
