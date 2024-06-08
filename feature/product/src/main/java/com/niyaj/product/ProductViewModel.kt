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

package com.niyaj.product

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.ProductRepository
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel() {
    override var totalItems: List<Int> = emptyList()

    private val _selectedCategory = MutableStateFlow(0)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val observableSearchText = snapshotFlow { searchText.value }

    @OptIn(ExperimentalCoroutinesApi::class)
    val products = observableSearchText.combine(_selectedCategory) { text, category ->
        productRepository.getAllProduct(text, category)
    }.flatMapLatest { it ->
        it.map { items ->
            totalItems = items.map { it.productId }

            if (items.isEmpty()) {
                UiState.Empty
            } else {
                UiState.Success(items)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    val categories = productRepository.getAllCategory().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = persistentListOf(),
    )

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch {
            when (val result = productRepository.deleteProducts(selectedItems.toList())) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable to delete products"))
                }

                is Resource.Success -> {
                    mEventFlow.emit(UiEvent.OnSuccess("${selectedItems.size} products has been deleted"))
                    analyticsHelper.logDeletedProducts(selectedItems.toList())
                }
            }

            mSelectedItems.clear()
        }
    }

    fun selectCategory(categoryId: Int) {
        viewModelScope.launch {
            if (_selectedCategory.value == categoryId) {
                _selectedCategory.value = 0
            } else {
                _selectedCategory.value = categoryId
            }
        }
    }
}

internal fun AnalyticsHelper.logDeletedProducts(data: List<Int>) {
    logEvent(
        event = AnalyticsEvent(
            type = "products_deleted",
            extras = listOf(
                com.niyaj.core.analytics.AnalyticsEvent.Param("products_deleted", data.toString()),
            ),
        ),
    )
}
