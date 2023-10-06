package com.niyaj.address.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.getAllCapitalizedLetters
import com.niyaj.data.repository.AddressRepository
import com.niyaj.data.repository.validation.AddressValidationRepository
import com.niyaj.model.Address
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AddEditAddressViewModel @Inject constructor(
    private val addressRepository: AddressRepository,
    private val validationRepository: AddressValidationRepository,
    @Dispatcher(PoposDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var addressId = savedStateHandle.get<Int>("addressId")

    var state by mutableStateOf(AddEditAddressState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<Int>("addressId")?.let { addressId ->
            getAddressById(addressId)
        }
    }


    val nameError: StateFlow<String?> = snapshotFlow { state.addressName }
        .mapLatest {
            validationRepository.validateAddressName(it, addressId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val shortNameError: StateFlow<String?> = snapshotFlow { state.shortName }
        .mapLatest {
            validationRepository.validateAddressShortName(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun onEvent(event: AddEditAddressEvent) {
        when (event) {
            is AddEditAddressEvent.AddressNameChanged -> {
                state = state.copy(
                    addressName = event.addressName,
                    shortName = getAllCapitalizedLetters(event.addressName)
                )
            }

            is AddEditAddressEvent.ShortNameChanged -> {
                state = state.copy(shortName = event.shortName)

            }

            is AddEditAddressEvent.CreateOrUpdateAddress -> {
                createOrUpdateAddress(event.addressId)
            }
        }
    }

    private fun createOrUpdateAddress(addressId: Int = 0) {
        viewModelScope.launch(ioDispatcher) {
            if (nameError.value == null && shortNameError.value == null) {
                val address = Address(
                    addressId = addressId,
                    addressName = state.addressName,
                    shortName = state.shortName,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = if (addressId != 0) System.currentTimeMillis() else null
                )

                when (val result = addressRepository.upsertAddress(address)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError(result.message!!))
                    }

                    is Resource.Success -> {
                        _eventFlow.emit(
                            UiEvent.OnSuccess("Address Created Successfully.")
                        )
                    }
                }

                state = AddEditAddressState()
            }
        }
    }

    private fun getAddressById(itemId: Int) {
        viewModelScope.launch(ioDispatcher) {
            when (val result = addressRepository.getAddressById(itemId)) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError("Unable to find address"))
                }

                is Resource.Success -> {
                    result.data?.let { address ->
                        addressId = address.addressId

                        state = state.copy(
                            shortName = address.shortName,
                            addressName = address.addressName,
                        )
                    }
                }
            }
        }
    }
    
}