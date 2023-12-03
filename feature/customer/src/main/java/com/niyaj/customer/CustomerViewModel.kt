package com.niyaj.customer

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.CustomerRepository
import com.niyaj.domain.use_cases.DeleteCustomersUseCase
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val customerRepository: CustomerRepository,
    private val deleteCustomersUseCase: DeleteCustomersUseCase,
) : BaseViewModel() {

    override var totalItems: List<Int> = emptyList()

    @OptIn(ExperimentalCoroutinesApi::class)
    val customers = snapshotFlow { searchText.value }
        .flatMapLatest { it ->
            customerRepository.getAllCustomer(it)
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

        viewModelScope.launch {
            when (deleteCustomersUseCase(selectedItems.toList())) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError("Unable to delete customer"))
                }

                is Resource.Success -> {
                    mEventFlow.emit(
                        UiEvent.OnSuccess(
                            "${selectedItems.size} customer has been deleted"
                        )
                    )
                }
            }

            mSelectedItems.clear()
        }
    }
}