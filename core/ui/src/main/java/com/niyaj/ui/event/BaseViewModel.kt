/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.ui.event

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor() : ViewModel() {

    private val _showSearchBar = MutableStateFlow(false)
    val showSearchBar = _showSearchBar.asStateFlow()

    val mSearchText = mutableStateOf("")
    val searchText: State<String> = mSearchText

    val mSelectedItems = mutableStateListOf<Int>()
    val selectedItems: SnapshotStateList<Int> = mSelectedItems

    open var totalItems: List<Int> = emptyList()

    val mEventFlow =
        MutableSharedFlow<UiEvent>()
    val eventFlow = mEventFlow.asSharedFlow()

    private var count: Int = 0

    open fun selectItem(itemId: Int) {
        viewModelScope.launch {
            if (mSelectedItems.contains(itemId)) {
                mSelectedItems.remove(itemId)
            } else {
                mSelectedItems.add(itemId)
            }
        }
    }

    open fun selectAllItems() {
        viewModelScope.launch {
            count += 1

            if (totalItems.isNotEmpty()) {
                if (totalItems.size == mSelectedItems.size) {
                    mSelectedItems.clear()
                } else {
                    totalItems.forEach { itemId ->
                        if (count % 2 != 0) {
                            val selectedProduct = mSelectedItems.find { it == itemId }

                            if (selectedProduct == null) {
                                mSelectedItems.add(itemId)
                            }
                        } else {
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

    open fun deleteItems() {}

    open fun openSearchBar() {
        viewModelScope.launch {
            _showSearchBar.emit(true)
        }
    }

    open fun searchTextChanged(text: String) {
        viewModelScope.launch {
            mSearchText.value = text
        }
    }

    open fun clearSearchText() {
        viewModelScope.launch {
            mSearchText.value = ""
        }
    }

    open fun closeSearchBar() {
        viewModelScope.launch {
            mSearchText.value = ""
            _showSearchBar.emit(false)
        }
    }

}