/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.category.createOrUpdate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.capitalizeWords
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.CategoryRepository
import com.niyaj.domain.category.ValidateCategoryNameUseCase
import com.niyaj.model.Category
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.annotations.TestOnly
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class AddEditCategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val validateCategoryName: ValidateCategoryNameUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {

    private var categoryId = savedStateHandle.getStateFlow("categoryId", 0)

    var state by mutableStateOf(AddEditCategoryState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.shareIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        replay = 1,
    )

    init {
        savedStateHandle.get<Int>("categoryId")?.let { categoryId ->
            if (categoryId != 0) getCategoryById(categoryId)
        }
    }

    val nameError: StateFlow<String?> = snapshotFlow { state.categoryName }
        .mapLatest {
            validateCategoryName(it, categoryId.value).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    fun onEvent(event: AddEditCategoryEvent) {
        when (event) {
            is AddEditCategoryEvent.CategoryNameChanged -> {
                state = state.copy(categoryName = event.categoryName)
            }

            is AddEditCategoryEvent.CategoryAvailabilityChanged -> {
                state = state.copy(isAvailable = !state.isAvailable)
            }

            is AddEditCategoryEvent.CreateUpdateAddEditCategory -> {
                createOrUpdateCategory(categoryId.value)
            }
        }
    }

    private fun getCategoryById(itemId: Int) {
        viewModelScope.launch {
            when (val result = categoryRepository.getCategoryById(itemId)) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError(result.message ?: "unable"))
                }

                is Resource.Success -> {
                    result.data?.let { category ->
                        state = state.copy(
                            categoryName = category.categoryName,
                            isAvailable = category.isAvailable,
                        )
                    }
                }
            }
        }
    }

    private fun createOrUpdateCategory(categoryId: Int = 0) {
        viewModelScope.launch {
            if (nameError.value == null) {
                val addOnItem = Category(
                    categoryId = categoryId,
                    categoryName = state.categoryName.trimEnd().capitalizeWords,
                    isAvailable = state.isAvailable,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = if (categoryId != 0) System.currentTimeMillis() else null,
                )

                when (categoryRepository.upsertCategory(addOnItem)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError("Unable To Create Category."))
                    }

                    is Resource.Success -> {
                        val message = if (categoryId == 0) "Created" else "Updated"
                        _eventFlow.emit(UiEvent.OnSuccess("Category $message Successfully."))
                        analyticsHelper.logOnCreateOrUpdateCategory(categoryId, message)
                    }
                }
                state = AddEditCategoryState()
            }
        }
    }

    @TestOnly
    internal fun setCategoryId(id: Int) {
        savedStateHandle["categoryId"] = id
    }
}

private fun AnalyticsHelper.logOnCreateOrUpdateCategory(categoryId: Int, message: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "category_$message",
            extras = listOf(AnalyticsEvent.Param("category_$message", categoryId.toString())),
        ),
    )
}
