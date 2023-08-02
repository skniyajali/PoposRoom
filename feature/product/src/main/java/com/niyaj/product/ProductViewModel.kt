package com.niyaj.product

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.ProductRepository
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
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
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : BaseViewModel() {
    override var totalItems: List<Int> = emptyList()

    private val _selectedCategory = MutableStateFlow(0)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _text = snapshotFlow { searchText.value }

    @OptIn(ExperimentalCoroutinesApi::class)
    val products = _text.combine(_selectedCategory) { text, category ->
        productRepository.getAllProduct(text, category)
    }.flatMapLatest { it ->
        it.map { items ->
            totalItems = items.map { it.productId }

            if (items.isEmpty()) {
                UiState.Empty
            } else UiState.Success(items)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading
    )

    val categories = productRepository.getAllCategory().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch(ioDispatcher) {
            when (val result = productRepository.deleteProducts(selectedItems.toList())) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable to delete products"))
                }

                is Resource.Success -> {
                    mEventFlow.emit(UiEvent.OnSuccess("${selectedItems.size} products has been deleted"))
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