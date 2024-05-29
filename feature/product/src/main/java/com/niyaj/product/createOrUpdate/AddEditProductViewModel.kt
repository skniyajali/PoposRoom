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

package com.niyaj.product.createOrUpdate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.capitalizeWords
import com.niyaj.common.utils.safeInt
import com.niyaj.common.utils.safeString
import com.niyaj.data.repository.ProductRepository
import com.niyaj.data.repository.validation.ProductValidationRepository
import com.niyaj.model.Category
import com.niyaj.model.Product
import com.niyaj.ui.utils.UiEvent
import com.samples.apps.core.analytics.AnalyticsEvent
import com.samples.apps.core.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val validationRepository: ProductValidationRepository,
    private val analyticsHelper: AnalyticsHelper,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val productId = savedStateHandle.get<Int>("productId")

    var state by mutableStateOf(AddEditProductState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _selectedCategory = MutableStateFlow(Category())
    val selectedCategory = _selectedCategory.asStateFlow()

    val categories = productRepository.getAllCategory().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val categoryError: StateFlow<String?> = _selectedCategory
        .mapLatest {
            validationRepository.validateCategoryId(it.categoryId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val nameError: StateFlow<String?> = snapshotFlow { state.productName }
        .mapLatest {
            validationRepository.validateProductName(it, productId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val priceError: StateFlow<String?> = snapshotFlow { state.productPrice }
        .mapLatest {
            validationRepository.validateProductPrice(safeString(it)).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    init {
        savedStateHandle.get<Int>("productId")?.let { productId ->
            getProductById(productId)
        }
    }

    fun onEvent(event: AddEditProductEvent) {
        when (event) {
            is AddEditProductEvent.CategoryChanged -> {
                viewModelScope.launch {
                    _selectedCategory.value = event.category
                }
            }

            is AddEditProductEvent.ProductNameChanged -> {
                viewModelScope.launch {
                    state = state.copy(
                        productName = event.productName,
                    )
                }
            }

            is AddEditProductEvent.ProductPriceChanged -> {
                viewModelScope.launch {
                    state = state.copy(
                        productPrice = event.productPrice,
                    )
                }
            }

            is AddEditProductEvent.ProductDescChanged -> {
                viewModelScope.launch {
                    state = state.copy(
                        productDesc = event.productDesc,
                    )
                }
            }

            is AddEditProductEvent.ProductAvailabilityChanged -> {
                viewModelScope.launch {
                    state = state.copy(
                        productAvailability = !state.productAvailability,
                    )
                }
            }

            is AddEditProductEvent.AddOrUpdateProduct -> {
                createOrUpdateProduct(event.productId)
            }
        }
    }

    private fun createOrUpdateProduct(productId: Int = 0) {
        viewModelScope.launch {
            val hasError = listOf(nameError, priceError, categoryError).all {
                it.value != null
            }
            val message = if (productId == 0) "created" else "updated"

            if (!hasError) {
                val newProduct = Product(
                    productId = productId,
                    categoryId = _selectedCategory.value.categoryId,
                    productName = state.productName.trim().capitalizeWords,
                    productPrice = state.productPrice.safeInt(),
                    productDescription = state.productDesc.trim().capitalizeWords,
                    productAvailability = state.productAvailability,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = if (productId == 0) null else System.currentTimeMillis(),
                )

                when (productRepository.upsertProduct(newProduct)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError("Unable to $message product"))
                    }

                    is Resource.Success -> {
                        _eventFlow.emit(UiEvent.OnSuccess("Product $message successfully"))
                        analyticsHelper.logOnCreateOrUpdateProduct(productId, message)
                    }
                }
            }
        }
    }

    private fun getProductById(productId: Int) {
        viewModelScope.launch {
            when (val result = productRepository.getProductById(productId)) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError("Unable to retrieve product"))
                }

                is Resource.Success -> {
                    result.data?.let { product ->
                        getCategoryById(product.categoryId)

                        state = state.copy(
                            productName = product.productName,
                            productPrice = product.productPrice.toString(),
                            productDesc = product.productDescription,
                            productAvailability = product.productAvailability,
                        )
                    }
                }
            }
        }
    }

    private fun getCategoryById(categoryId: Int) {
        viewModelScope.launch {
            productRepository.getCategoryById(categoryId)?.let { category ->
                _selectedCategory.value = category
            }
        }
    }
}

private fun AnalyticsHelper.logOnCreateOrUpdateProduct(data: Int, message: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "product_$message",
            extras = listOf(
                AnalyticsEvent.Param("product_$message", data.toString()),
            ),
        ),
    )
}
