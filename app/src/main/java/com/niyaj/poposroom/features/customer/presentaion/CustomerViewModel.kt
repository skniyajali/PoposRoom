package com.niyaj.poposroom.features.customer.presentaion

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.poposroom.features.common.event.ItemEventsViewModel
import com.niyaj.poposroom.features.common.event.UiState
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.customer.dao.CustomerDao
import com.niyaj.poposroom.features.customer.domain.use_cases.GetAllCustomers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val customerDao: CustomerDao,
    private val getAllCustomers: GetAllCustomers,
    @Dispatcher(PoposDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
): ItemEventsViewModel() {

    override var totalItems: List<Int> = emptyList()

    @OptIn(ExperimentalCoroutinesApi::class)
    val charges = snapshotFlow { searchText.value }
        .flatMapLatest { it ->
            getAllCustomers(it)
                .onStart { UiState.Loading }
                .map { items ->
                    totalItems = items.map { it.customerId }
                    if (items.isEmpty()) {
                        UiState.Empty
                    } else UiState.Success(items)
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch(ioDispatcher) {
            val result = customerDao.deleteCustomer(selectedAddOnItems.toList())
            mSelectedAddOnItems.clear()

            if (result != 0) {
                mEventFlow.emit(UiEvent.OnSuccess("$result charges has been deleted"))
            } else {
                mEventFlow.emit(UiEvent.OnError("Unable to delete charges"))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}