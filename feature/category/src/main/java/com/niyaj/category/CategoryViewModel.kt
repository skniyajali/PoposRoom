package com.niyaj.category

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.CategoryRepository
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import com.samples.apps.core.analytics.AnalyticsEvent
import com.samples.apps.core.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel() {

    override var totalItems: List<Int> = emptyList()

    val addOnItems = snapshotFlow { searchText.value }
        .flatMapLatest { it ->
            categoryRepository.getAllCategory(it)
                .onStart { UiState.Loading }
                .map { items ->
                    totalItems = items.map { it.categoryId }
                    if (items.isEmpty()) {
                        UiState.Empty
                    } else UiState.Success(items)
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading,
        )

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch {
            when (val result = categoryRepository.deleteCategories(selectedItems.toList())) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.OnError(result.message!!))
                }

                is Resource.Success -> {
                    mEventFlow.emit(
                        UiEvent.OnSuccess(
                            "${selectedItems.size} category has been deleted",
                        ),
                    )

                    analyticsHelper.logDeletedCategories(selectedItems.toList())
                }
            }
            mSelectedItems.clear()
        }
    }
}

internal fun AnalyticsHelper.logDeletedCategories(categories: List<Int>) {
    logEvent(
        event = AnalyticsEvent(
            type = "category_deleted",
            extras = listOf(
                AnalyticsEvent.Param("category_deleted", categories.toString()),
            ),
        ),
    )
}