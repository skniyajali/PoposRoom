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
import androidx.compose.runtime.mutableStateListOf
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
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.ProductRepository
import com.niyaj.domain.product.ValidateProductCategoryUseCase
import com.niyaj.domain.product.ValidateProductNameUseCase
import com.niyaj.domain.product.ValidateProductPriceUseCase
import com.niyaj.domain.product.ValidateProductTagUseCase
import com.niyaj.model.Category
import com.niyaj.model.Product
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val validateProductCategory: ValidateProductCategoryUseCase,
    private val validateProductName: ValidateProductNameUseCase,
    private val validateProductTag: ValidateProductTagUseCase,
    private val validateProductPrice: ValidateProductPriceUseCase,
    private val analyticsHelper: AnalyticsHelper,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val productId = savedStateHandle.get<Int>("productId") ?: 0

    var state by mutableStateOf(AddEditProductState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _selectedCategory = MutableStateFlow(Category())
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _tagList = mutableStateListOf<String>()
    val tagList: MutableList<String> = _tagList

    private val _selectedTags = mutableStateListOf("")
    val selectedTags: MutableList<String> = _selectedTags

    val categories = productRepository.getAllCategory().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val categoryError: StateFlow<String?> = _selectedCategory
        .mapLatest {
            validateProductCategory(it.categoryId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val nameError: StateFlow<String?> = snapshotFlow { state.productName }
        .mapLatest {
            validateProductName(it, productId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val priceError: StateFlow<String?> = snapshotFlow { state.productPrice }
        .mapLatest {
            validateProductPrice(safeString(it)).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val tagError = snapshotFlow { state.tagName }.mapLatest {
        validateProductTag(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    init {
        _tagList.addAll(defaultTagList)

        savedStateHandle.get<Int>("productId")?.let { productId ->
            if (productId != 0) getProductById(productId)
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

            is AddEditProductEvent.TagNameChanged -> {
                state = state.copy(tagName = event.tagName)
            }

            is AddEditProductEvent.OnSelectTag -> {
                viewModelScope.launch {
                    if (!_tagList.contains(event.tagName)) {
                        _tagList.add(event.tagName)
                    }

                    if (_selectedTags.contains(event.tagName)) {
                        _selectedTags.remove(event.tagName)
                    } else {
                        _selectedTags.add(event.tagName)
                    }

                    if (state.tagName == event.tagName) {
                        state = state.copy(tagName = "")
                    }
                }
            }

            is AddEditProductEvent.AddOrUpdateProduct -> {
                createOrUpdateProduct(productId)
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
                    tags = selectedTags.toList(),
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

                        if (product.tags.isNotEmpty()) {
                            _selectedTags.clear()

                            product.tags.forEach {
                                if (!tagList.contains(it)) {
                                    tagList.add(it)
                                }
                            }

                            _selectedTags.addAll(product.tags)
                        }
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

internal val defaultTagList = listOf(
    "Popular",
    "Best",
    "New",
    "Trending",
    "Top",
)

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
