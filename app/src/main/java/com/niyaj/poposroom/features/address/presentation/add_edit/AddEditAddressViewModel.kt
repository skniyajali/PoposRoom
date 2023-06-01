package com.niyaj.poposroom.features.address.presentation.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.poposroom.features.address.dao.AddressDao
import com.niyaj.poposroom.features.address.domain.model.Address
import com.niyaj.poposroom.features.address.domain.validation.AddressValidationRepository
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.common.utils.getAllCapitalizedLetters
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
import java.util.Date
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AddEditAddressViewModel @Inject constructor(
    private val addressDao: AddressDao,
    private val validationRepository: AddressValidationRepository,
    @Dispatcher(PoposDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle
): ViewModel() {

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
            validationRepository.validateAddressName(addressId, it).errorMessage
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
        when(event) {
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
                    createdAt = Date(),
                    updatedAt = if (addressId != 0) Date() else null
                )

                val result = addressDao.upsertAddress(address)

                if (result != 0L) {
                    _eventFlow.emit(UiEvent.OnSuccess("Address Created Successfully."))
                }else {
                    _eventFlow.emit(UiEvent.OnError("Unable To Create Address."))

                }

                state = AddEditAddressState()
            }
        }
    }

    fun getAddressById(itemId: Int){
        viewModelScope.launch(ioDispatcher) {
            addressDao.getAddressById(itemId)?.let { address ->
                addressId = address.addressId

                state = state.copy(
                    shortName = address.shortName,
                    addressName = address.addressName,
                )
            }
        }
    }

    fun resetFields() {
        state = AddEditAddressState()
    }

}