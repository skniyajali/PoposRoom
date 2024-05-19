package com.niyaj.address

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.AddressRepository
import com.niyaj.domain.use_cases.DeleteAddressesUseCase
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import com.samples.apps.core.analytics.AnalyticsEvent
import com.samples.apps.core.analytics.AnalyticsHelper
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
class AddressViewModel @Inject constructor(
    private val addressRepository: AddressRepository,
    private val deleteAddressesUseCase: DeleteAddressesUseCase,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel() {
    override var totalItems: List<Int> = emptyList()

    @OptIn(ExperimentalCoroutinesApi::class)
    val addresses = snapshotFlow { searchText.value }
        .flatMapLatest { it ->
            addressRepository.getAllAddress(it)
                .onStart { UiState.Loading }
                .map { items ->
                    totalItems = items.map { it.addressId }
                    if (items.isEmpty()) {
                        UiState.Empty
                    } else UiState.Success(items)
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading,
        )

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch {

            when (val result = deleteAddressesUseCase(selectedItems.toList())) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                }

                is Resource.Success -> {
                    mEventFlow.emit(
                        UiEvent.OnSuccess(
                            "${selectedItems.size} address has been deleted",
                        ),
                    )
                    analyticsHelper.logDeletedAddress(selectedItems.toList())
                }
            }

            mSelectedItems.clear()
        }
    }
}

internal fun AnalyticsHelper.logDeletedAddress(data: List<Int>) {
    logEvent(
        event = AnalyticsEvent(
            type = "address_deleted",
            extras = listOf(
                AnalyticsEvent.Param("address_deleted", data.toString()),
            ),
        ),
    )
}