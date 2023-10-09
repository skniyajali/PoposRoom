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
import com.niyaj.data.repository.MarketListRepository
import com.niyaj.data.repository.validation.MarketListValidationRepository
import com.niyaj.model.MarketList
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
class AddEditMarketListViewModel @Inject constructor(
    private val repository: MarketListRepository,
    private val validationRepository: MarketListValidationRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val itemId = savedStateHandle.get<Int>("itemId") ?: 0

    var state by mutableStateOf(AddEditMarketListState())

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

    fun onEvent(event: AddEditMarketListEvent) {
        when (event) {
            is AddEditMarketListEvent.ItemTypeChanged -> {
                state = state.copy(itemType = event.type)
            }

            is AddEditMarketListEvent.ItemNameChanged -> {
                state = state.copy(itemName = event.name)
            }

            is AddEditMarketListEvent.ItemMeasureUnitChanged -> {
                state = state.copy(itemMeasureUnit = event.unit)
            }

            is AddEditMarketListEvent.ItemDescriptionChanged -> {
                state = state.copy(itemDesc = event.description)
            }

            is AddEditMarketListEvent.ItemPriceChanged -> {
                state = state.copy(itemPrice = event.price.safeString())
            }

            is AddEditMarketListEvent.AddOrUpdateItem -> {
                createOrUpdateItem(itemId)
            }

        }
    }

    private fun getMarketListById(itemId: Int) {
        viewModelScope.launch {
            repository.getMarketListById(itemId).data?.let {
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
                val newItem = MarketList(
                    itemId = itemId,
                    itemType = state.itemType,
                    itemName = state.itemName,
                    itemPrice = state.itemPrice,
                    itemDescription = state.itemDesc,
                    itemMeasureUnit = state.itemMeasureUnit,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = if (itemId != 0) System.currentTimeMillis() else null
                )

                when (val result = repository.upsertMarketList(newItem)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError(result.message ?: "Invalid"))
                    }

                    is Resource.Success -> {
                        _eventFlow.emit(
                            UiEvent.OnSuccess("Item has been successfully created or updated")
                        )
                    }
                }

                state = AddEditMarketListState()
            }
        }
    }

}