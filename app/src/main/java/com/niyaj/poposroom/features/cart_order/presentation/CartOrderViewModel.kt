package com.niyaj.poposroom.features.cart_order.presentation

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.poposroom.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.poposroom.features.common.event.BaseViewModel
import com.niyaj.poposroom.features.common.event.UiState
import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.selected.domain.model.Selected
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartOrderViewModel @Inject constructor(
    private val cartOrderRepository: CartOrderRepository
) : BaseViewModel() {

    override var totalItems: List<Int> = emptyList()

    val text = snapshotFlow { _searchText.value }

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedId = cartOrderRepository.getSelectedCartOrder()
        .mapLatest {
            it?.orderId ?: 0
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val cartOrders = snapshotFlow { _searchText.value }
        .flatMapLatest { text ->
            cartOrderRepository.getAllCartOrders(text).mapLatest { list ->
                val data = list.sortedByDescending { it.orderId == selectedId.value }

                totalItems = data.map { it.orderId }
                if (data.isEmpty()) {
                    UiState.Empty
                } else UiState.Success(data)

            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )


    fun selectCartOrder() {
        viewModelScope.launch {
            val result = cartOrderRepository.insertOrUpdateSelectedOrder(
                Selected(orderId = selectedItems.first())
            )

            when (result) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                }

                is Resource.Success -> {
                    deselectItems()
                }
            }
        }
    }

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch {
            when (val result = cartOrderRepository.deleteCartOrders(selectedItems.toList())) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                }

                is Resource.Success -> {
                    mEventFlow.emit(
                        UiEvent.OnSuccess(
                            "${selectedItems.size} cart orders has been deleted"
                        )
                    )
                }
            }

            mSelectedItems.clear()
        }
    }
}