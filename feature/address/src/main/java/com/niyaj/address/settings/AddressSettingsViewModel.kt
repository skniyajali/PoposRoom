package com.niyaj.address.settings

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.AddressRepository
import com.niyaj.model.Address
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressSettingsViewModel @Inject constructor(
    private val addressRepository: AddressRepository
): BaseViewModel() {

    val addresses = snapshotFlow { mSearchText.value }.flatMapLatest {
        addressRepository.getAllAddress(it)
    }.mapLatest { list ->
        totalItems = list.map { it.addressId }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _exportedItems = MutableStateFlow<List<Address>>(emptyList())
    val exportedItems = _exportedItems.asStateFlow()

    private val _importedItems = MutableStateFlow<List<Address>>(emptyList())
    val importedItems = _importedItems.asStateFlow()

    fun onEvent(event: AddressSettingsEvent) {
        when(event) {
            is AddressSettingsEvent.GetExportedItems -> {
                viewModelScope.launch {
                    if (mSelectedItems.isEmpty()) {
                        _exportedItems.value = addresses.value
                    } else {
                        val list = mutableListOf<Address>()

                        mSelectedItems.forEach { id ->
                            val address = addresses.value.find { it.addressId == id }

                            if (address != null) {
                                list.add(address)
                            }
                        }

                        _exportedItems.emit(list.toList())
                    }
                }
            }

            is AddressSettingsEvent.OnImportAddressItemsFromFile -> {
                viewModelScope.launch {
                    _importedItems.value = emptyList()

                    if (event.data.isNotEmpty()) {
                        totalItems = event.data.map { it.addressId }
                        _importedItems.value = event.data
                    }
                }
            }

            is AddressSettingsEvent.ImportAddressItemsToDatabase -> {
                viewModelScope.launch {
                    val data = if (mSelectedItems.isNotEmpty()) {
                        mSelectedItems.flatMap {addressId ->
                            _importedItems.value.filter { it.addressId == addressId }
                        }
                    }else {
                        _importedItems.value
                    }

                    when(val result = addressRepository.importAddressesToDatabase(data)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                        }
                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.OnSuccess("${data.size} items has been imported successfully"))
                        }
                    }
                }
            }
        }
    }
}