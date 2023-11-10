package com.niyaj.home

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.CartRepository
import com.niyaj.data.repository.HomeRepository
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainFeedViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val cartRepository: CartRepository,
): BaseViewModel() {

    override var totalItems: List<Int> = emptyList()

    private val _selectedCategory = MutableStateFlow(0)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _text = snapshotFlow { mSearchText.value }

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedId = repository.getSelectedOrder()
        .mapLatest {
            if (it?.orderId != null){
                val address = repository.getSelectedOrderAddress(it.orderId)
                SelectedOrderState(
                    orderId = it.orderId,
                    addressName = address ?: ""
                )
            } else SelectedOrderState()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SelectedOrderState()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val products = _text.combine(_selectedCategory) { text, category ->
        repository.getAllProduct(text, category)
    }.flatMapLatest { it ->
            it.map { items ->
                totalItems = items.take(1).map { it.productId }

                if (items.isEmpty()) {
                    UiState.Empty
                } else UiState.Success(items)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    val categories = repository.getAllCategory().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )


    fun selectCategory(categoryId: Int) {
        viewModelScope.launch {
            if (_selectedCategory.value == categoryId) {
                _selectedCategory.value = 0
            }else {
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
                }
            }
        }
    }
}