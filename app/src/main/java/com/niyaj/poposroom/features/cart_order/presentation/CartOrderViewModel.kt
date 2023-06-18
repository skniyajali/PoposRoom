package com.niyaj.poposroom.features.cart_order.presentation

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.poposroom.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.poposroom.features.common.event.BaseViewModel
import com.niyaj.poposroom.features.common.event.UiState
import com.niyaj.poposroom.features.common.utils.Resource
import com.niyaj.poposroom.features.common.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartOrderViewModel @Inject constructor(
    private val cartOrderRepository: CartOrderRepository
) : BaseViewModel() {

    override var totalItems: List<Int> = emptyList()

    @OptIn(ExperimentalCoroutinesApi::class)
    val cartOrders = snapshotFlow { searchText.value }
        .flatMapLatest { it ->
            cartOrderRepository.getAllCartOrders(it).map { list ->
                totalItems = list.map { it.cartOrderId }
                if (list.isEmpty()) {
                    UiState.Empty
                } else UiState.Success(list)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )


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