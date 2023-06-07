package com.niyaj.poposroom.features.common.event

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.poposroom.features.common.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class ItemEventsViewModel @Inject constructor(): ViewModel() {

    private val _showSearchBar = MutableStateFlow(false)
    val showSearchBar = _showSearchBar.asStateFlow()

    private val _searchText = mutableStateOf("")
    val searchText: State<String> = _searchText

    val mSelectedItems  =  mutableStateListOf<Int>()
    val selectedItems: SnapshotStateList<Int> = mSelectedItems

    open var totalItems: List<Int> = emptyList()

    val mEventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = mEventFlow.asSharedFlow()

    private var count: Int = 0

    open fun selectItem(itemId: Int) {
        viewModelScope.launch {
            if(mSelectedItems.contains(itemId)){
                mSelectedItems.remove(itemId)
            }else{
                mSelectedItems.add(itemId)
            }
        }
    }

    open fun selectAllItems() {
        viewModelScope.launch {
            count += 1

            if (totalItems.isNotEmpty()){
                if (totalItems.size == mSelectedItems.size){
                    mSelectedItems.clear()
                }else{
                    totalItems.forEach { itemId ->
                        if (count % 2 != 0){
                            val selectedProduct = mSelectedItems.find { it == itemId }

                            if (selectedProduct == null){
                                mSelectedItems.add(itemId)
                            }
                        }else {
                            mSelectedItems.remove(itemId)
                        }
                    }
                }
            }
        }
    }

    open fun deselectItems() {
        mSelectedItems.clear()
    }

    open fun deleteItems() {

    }

    open fun openSearchBar() {
        viewModelScope.launch {
            _showSearchBar.emit(true)
        }
    }

    open fun searchTextChanged(text: String) {
        viewModelScope.launch {
            _searchText.value = text
        }
    }

    open fun clearSearchText() {
        viewModelScope.launch {
            _searchText.value = ""
        }
    }

    open fun closeSearchBar() {
        viewModelScope.launch {
            _searchText.value = ""
            _showSearchBar.emit(false)
        }
    }

    override fun onCleared() {
        super.onCleared()
    }
}