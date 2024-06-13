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

package com.niyaj.home

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.CartRepository
import com.niyaj.data.repository.HomeRepository
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val cartRepository: CartRepository,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel() {

    private val _selectedCategory = MutableStateFlow(0)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val observableSearchText = snapshotFlow { mSearchText.value }

    val selectedId = repository.getSelectedOrder()
        .mapLatest {
            if (it?.orderId != null) {
                val address = repository.getSelectedOrderAddress(it.orderId)
                SelectedOrderState(
                    orderId = it.orderId,
                    addressName = address ?: "",
                )
            } else {
                SelectedOrderState()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SelectedOrderState(),
        )

    val productsWithQuantity = observableSearchText.combine(_selectedCategory) { text, category ->
        repository.getProductsWithQuantities(text, category)
    }.distinctUntilChanged().flatMapLatest {
        it.map { items ->
            if (items.isEmpty()) UiState.Empty else UiState.Success(items.toImmutableList())
        }
    }.flowOn(ioDispatcher).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    val categories = repository.getAllCategory().map {
        if (it.isEmpty()) UiState.Empty else UiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    fun selectCategory(categoryId: Int) {
        viewModelScope.launch {
            if (_selectedCategory.value == categoryId) {
                _selectedCategory.value = 0
            } else {
                _selectedCategory.value = categoryId
            }
        }
    }

    fun addProductToCart(orderId: Int, productId: Int) {
        viewModelScope.launch {
            when (val result = cartRepository.addProductToCart(orderId, productId)) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable to add product"))
                }

                is Resource.Success -> {
                    mEventFlow.emit(UiEvent.OnSuccess("Product added to cart"))
                    analyticsHelper.logAddProductToCart(orderId, productId)
                }
            }
        }
    }

    fun removeProductFromCart(orderId: Int, productId: Int) {
        viewModelScope.launch {
            when (val result = cartRepository.removeProductFromCart(orderId, productId)) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable to add product"))
                }

                is Resource.Success -> {
                    mEventFlow.emit(UiEvent.OnSuccess("Product removed from cart"))
                    analyticsHelper.logRemoveProductFromCart(orderId, productId)
                }
            }
        }
    }
}

internal fun AnalyticsHelper.logAddProductToCart(orderId: Int, productId: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "product_added_to_cart",
            extras = listOf(
                AnalyticsEvent.Param("product_added_to_cart", "orderId - $orderId & productId - $productId"),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logRemoveProductFromCart(orderId: Int, productId: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "product_removed_from_cart",
            extras = listOf(
                AnalyticsEvent.Param("product_removed_from_cart", "orderId - $orderId & productId - $productId"),
            ),
        ),
    )
}
