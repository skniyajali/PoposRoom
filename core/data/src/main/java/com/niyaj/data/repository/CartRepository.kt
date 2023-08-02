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

    suspend fun addProductToCart(orderId: Int, productId: Int): Resource<Boolean>

    suspend fun removeProductFromCart(orderId: Int, productId: Int): Resource<Boolean>

    suspend fun placeOrder(orderId: Int): Resource<Boolean>

    suspend fun placeAllOrder(orderIds: List<Int>): Resource<Boolean>

    suspend fun updateAddOnItem(orderId: Int, itemId: Int): Resource<Boolean>

    suspend fun updateCharges(orderId: Int, chargesId: Int): Resource<Boolean>

    suspend fun deleteProductFromCart(orderId: Int, productId: Int): Resource<Boolean>

    suspend fun getProductQuantity(orderId: Int, productId: Int): Flow<Int>
}