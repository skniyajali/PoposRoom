package com.niyaj.poposroom.features.charges.presentation.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.poposroom.features.charges.dao.ChargesDao
import com.niyaj.poposroom.features.charges.domain.model.Charges
import com.niyaj.poposroom.features.charges.domain.validation.ChargesValidationRepository
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.common.utils.safeInt
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

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class AddEditChargesViewModel @Inject constructor(
    private val chargesDao: ChargesDao,
    private val validationRepository: ChargesValidationRepository,
    @Dispatcher(PoposDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private var chargesId = savedStateHandle.get<Int>("addOnItemId")

    var addEditState by mutableStateOf(AddEditChargesState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<Int>("addOnItemId")?.let { addOnItemId ->
            getChargesById(addOnItemId)
        }
    }

    val nameError: StateFlow<String?> = snapshotFlow { addEditState.chargesName }
        .mapLatest {
            validationRepository.validateChargesName(it, chargesId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val priceError: StateFlow<String?> = snapshotFlow { addEditState.chargesPrice }
        .mapLatest {
            validationRepository.validateChargesPrice(addEditState.chargesApplicable, addEditState.chargesPrice).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun onEvent(event: AddEditChargesEvent) {
        when (event) {
            is AddEditChargesEvent.ChargesNameChanged -> {
                addEditState = addEditState.copy(chargesName = event.chargesName)
            }

            is AddEditChargesEvent.ChargesPriceChanged -> {
                addEditState = addEditState.copy(chargesPrice = safeInt(event.chargesPrice))

            }

            is AddEditChargesEvent.ChargesApplicableChanged -> {
                addEditState = addEditState.copy(chargesApplicable = !addEditState.chargesApplicable)
            }

            is AddEditChargesEvent.CreateOrUpdateCharges -> {
                createOrUpdateCharges(event.chargesId)
            }
        }
    }

    fun getChargesById(itemId: Int) {
        viewModelScope.launch(ioDispatcher) {
            chargesDao.getChargesById(itemId)?.let { charges ->
                chargesId = charges.chargesId

                addEditState = addEditState.copy(
                    chargesName = charges.chargesName,
                    chargesPrice = charges.chargesPrice,
                    chargesApplicable = charges.isApplicable
                )
            }
        }
    }

    fun resetFields() {
        addEditState = AddEditChargesState()
    }

    private fun createOrUpdateCharges(chargesId: Int = 0) {
        viewModelScope.launch(ioDispatcher) {
            if (nameError.value == null && priceError.value == null) {
                val addOnItem = Charges(
                    chargesId = chargesId,
                    chargesName = addEditState.chargesName,
                    chargesPrice = addEditState.chargesPrice,
                    isApplicable = addEditState.chargesApplicable,
                    createdAt = Date(),
                    updatedAt = if (chargesId != 0) Date() else null
                )

                val result = chargesDao.upsertCharges(addOnItem)

                if (result != 0L) {
                    _eventFlow.emit(UiEvent.OnSuccess("AddOn Item Created Successfully."))
                }else {
                    _eventFlow.emit(UiEvent.OnError("Unable To Create AddOn Item."))

                }

                addEditState = AddEditChargesState()
            }
        }
    }
}