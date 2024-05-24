/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.model.AddOnItem
import com.niyaj.model.CartItem
import com.niyaj.model.OrderWithCartItems
import kotlinx.coroutines.flow.Flow

interface CartRepository {

    fun getAllAddOnItems(): Flow<List<AddOnItem>>

    suspend fun getAllCartOrders(): Flow<List<OrderWithCartItems>>

    suspend fun getAllDineInCart(): Flow<List<CartItem>>

    suspend fun getAllDineOutCart(): Flow<List<CartItem>>

    suspend fun getCartItemByOrderId(orderId: Int): Flow<CartItem>

    suspend fun addProductToCart(orderId: Int, productId: Int): Resource<Boolean>

    suspend fun removeProductFromCart(orderId: Int, productId: Int): Resource<Boolean>

    suspend fun placeOrder(orderId: Int): Resource<Boolean>

    suspend fun placeAllOrder(orderIds: List<Int>): Resource<Boolean>

    suspend fun updateAddOnItem(orderId: Int, itemId: Int): Resource<Boolean>

    suspend fun updateCharges(orderId: Int, chargesId: Int): Resource<Boolean>

    suspend fun deleteProductFromCart(orderId: Int, productId: Int): Resource<Boolean>

    suspend fun getProductQuantity(orderId: Int, productId: Int): Flow<Int>
}