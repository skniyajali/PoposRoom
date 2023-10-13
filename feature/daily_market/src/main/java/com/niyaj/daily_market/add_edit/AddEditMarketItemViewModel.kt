package com.niyaj.daily_market.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.measureUnitLists
import com.niyaj.common.utils.safeString
import com.niyaj.data.repository.MarketItemRepository
import com.niyaj.data.repository.validation.MarketItemValidationRepository
import com.niyaj.model.MarketItem
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditMarketItemViewModel @Inject constructor(
    private val repository: MarketItemRepository,
    private val validationRepository: MarketItemValidationRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val itemId = savedStateHandle.get<Int>("itemId") ?: 0

    var state by mutableStateOf(AddEditMarketItemState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<Int>("itemId")?.let {
            getMarketListById(it)
        }
    }

    val itemTypes = snapshotFlow { state.itemType }.flatMapLatest {
        repository.getAllItemType(it)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val measureUnits = snapshotFlow { state.itemMeasureUnit }.mapLatest { searchText ->
        measureUnitLists.filter { it.contains(searchText, true) }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val typeError = snapshotFlow { state.itemType }.mapLatest {
        validationRepository.validateItemType(it).errorMessage
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
    )

    val nameError = snapshotFlow { state.itemName }.mapLatest {
        validationRepository.validateItemName(it, itemId).errorMessage
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
    )

    val priceError = snapshotFlow { state.itemPrice }.mapLatest {
        validationRepository.validateItemPrice(it).errorMessage
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
    )

    val unitError = snapshotFlow { state.itemMeasureUnit }.mapLatest {
        validationRepository.validateItemMeasureUnit(it).errorMessage
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
    )

    fun onEvent(event: AddEditMarketItemEvent) {
        when (event) {
            is AddEditMarketItemEvent.ItemTypeChanged -> {
                state = state.copy(itemType = event.type)
            }

            is AddEditMarketItemEvent.ItemNameChanged -> {
                state = state.copy(itemName = event.name)
            }

            is AddEditMarketItemEvent.ItemMeasureUnitChanged -> {
                state = state.copy(itemMeasureUnit = event.unit)
            }

            is AddEditMarketItemEvent.ItemDescriptionChanged -> {
                state = state.copy(itemDesc = event.description)
            }

            is AddEditMarketItemEvent.ItemPriceChanged -> {
                state = state.copy(itemPrice = event.price.safeString())
            }

            is AddEditMarketItemEvent.AddOrUpdateItem -> {
                createOrUpdateItem(itemId)
            }

        }
    }

    private fun getMarketListById(itemId: Int) {
        viewModelScope.launch {
            repository.getMarketItemById(itemId).data?.let {
                state = state.copy(
                    itemType = it.itemType,
                    itemName = it.itemName,
                    itemPrice = it.itemPrice,
                    itemDesc = it.itemDescription,
                    itemMeasureUnit = it.itemMeasureUnit
                )
            }
        }
    }

    private fun createOrUpdateItem(itemId: Int) {
        viewModelScope.launch {
            val hasError =
                listOf(nameError, typeError, priceError, unitError).all { it.value != null }

            if (!hasError) {
                val newItem = MarketItem(
                    itemId = itemId,
                    itemType = state.itemType.trim(),
                    itemName = state.itemName.trim(),
                    itemPrice = state.itemPrice?.trim(),
                    itemDescription = state.itemDesc?.trim(),
                    itemMeasureUnit = state.itemMeasureUnit,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = if (itemId != 0) System.currentTimeMillis() else null
                )

                when (val result = repository.upsertMarketItem(newItem)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError(result.message ?: "Invalid"))
                    }

                    is Resource.Success -> {
                        _eventFlow.emit(
                            UiEvent.OnSuccess("Item has been successfully created or updated")
                        )
                    }
                }

                state = AddEditMarketItemState()
            }
        }
    }

}
