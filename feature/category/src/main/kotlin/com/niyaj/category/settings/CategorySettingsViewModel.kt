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

package com.niyaj.category.settings

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.core.analytics.AnalyticsEvent
import com.niyaj.core.analytics.AnalyticsEvent.Param
import com.niyaj.core.analytics.AnalyticsHelper
import com.niyaj.data.repository.CategoryRepository
import com.niyaj.model.Category
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
class CategorySettingsViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel() {

    val categories = snapshotFlow { mSearchText.value }.flatMapLatest {
        categoryRepository.getAllCategory(it)
    }.mapLatest { list ->
        totalItems = list.map { it.categoryId }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    private val _exportedCategories = MutableStateFlow<List<Category>>(emptyList())
    val exportedCategories = _exportedCategories.asStateFlow()

    private val _importedCategories = MutableStateFlow<List<Category>>(emptyList())
    val importedCategories = _importedCategories.asStateFlow()

    fun onEvent(event: CategorySettingsEvent) {
        when (event) {
            is CategorySettingsEvent.GetExportedCategory -> {
                viewModelScope.launch {
                    if (mSelectedItems.isEmpty()) {
                        _exportedCategories.value = categories.value
                    } else {
                        val list = mutableListOf<Category>()

                        mSelectedItems.forEach { id ->
                            val category = categories.value.find { it.categoryId == id }

                            if (category != null) {
                                list.add(category)
                            }
                        }

                        _exportedCategories.emit(list.toList())
                    }

                    analyticsHelper.logExportedCategoryToFile(_exportedCategories.value.size)
                }
            }

            is CategorySettingsEvent.OnImportCategoriesFromFile -> {
                viewModelScope.launch {
                    _importedCategories.value = emptyList()

                    if (event.data.isNotEmpty()) {
                        totalItems = event.data.map { it.categoryId }
                        _importedCategories.value = event.data

                        analyticsHelper.logImportedCategoryFromFile(event.data.size)
                    }
                }
            }

            is CategorySettingsEvent.ImportCategoriesToDatabase -> {
                viewModelScope.launch {
                    val data = if (mSelectedItems.isNotEmpty()) {
                        mSelectedItems.flatMap { categoryId ->
                            _importedCategories.value.filter { it.categoryId == categoryId }
                        }
                    } else {
                        _importedCategories.value
                    }

                    when (val result = categoryRepository.importCategoriesToDatabase(data)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                        }

                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.OnSuccess("${data.size} categories has been imported successfully"))
                            analyticsHelper.logImportedCategoryToDatabase(data.size)
                        }
                    }
                }
            }
        }
    }
}

internal fun AnalyticsHelper.logImportedCategoryFromFile(totalCategory: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "category_imported_from_file",
            extras = listOf(
                Param("category_imported_from_file", totalCategory.toString()),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logImportedCategoryToDatabase(totalCategory: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "category_imported_to_database",
            extras = listOf(
                Param("category_imported_to_database", totalCategory.toString()),
            ),
        ),
    )
}

internal fun AnalyticsHelper.logExportedCategoryToFile(totalCategory: Int) {
    logEvent(
        event = AnalyticsEvent(
            type = "category_exported_to_file",
            extras = listOf(
                Param("category_exported_to_file", totalCategory.toString()),
            ),
        ),
    )
}
