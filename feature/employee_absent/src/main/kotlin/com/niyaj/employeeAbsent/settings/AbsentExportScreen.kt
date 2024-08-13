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

package com.niyaj.employeeAbsent.settings

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_NOT_AVAILABLE
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.AbsentScreenTags.CREATE_NEW_ABSENT
import com.niyaj.common.tags.AbsentScreenTags.EXPORT_ABSENT_FILE_NAME
import com.niyaj.common.tags.AbsentScreenTags.EXPORT_ABSENT_TITLE
import com.niyaj.common.utils.Constants
import com.niyaj.common.utils.createExportNote
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.domain.utils.ImportExport
import com.niyaj.employeeAbsent.components.AbsentEmployeeList
import com.niyaj.employeeAbsent.destinations.AddEditAbsentScreenDestination
import com.niyaj.model.EmployeeWithAbsents
import com.niyaj.ui.components.ExportScaffold
import com.niyaj.ui.parameterProvider.AbsentPreviewData.employeesWithAbsents
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens.ABSENT_EXPORT_SCREEN
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Destination(route = ABSENT_EXPORT_SCREEN)
@Composable
fun AbsentExportScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: AbsentSettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val items by viewModel.items.collectAsStateWithLifecycle()
    val exportedItems by viewModel.exportedItems.collectAsStateWithLifecycle()
    val showSearchBar by viewModel.showSearchBar.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    val searchText = viewModel.searchText.value
    val selectedEmployee = viewModel.selectedEmployee.toList()
    val selectedItems = viewModel.selectedItems.toList()

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(data.errorMessage)
                }

                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(data.successMessage)
                }
            }
        }
    }

    val exportLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { activityResult ->
            activityResult.data?.data?.let {
                scope.launch {
                    val result = ImportExport.writeDataAsync(context, it, exportedItems)

                    if (result.isSuccess) {
                        val countItems = exportedItems.sumOf { it.absents.size }
                        resultBackNavigator.navigateBack("$countItems Items has been exported.")
                    } else {
                        resultBackNavigator.navigateBack("Unable to export items.")
                    }
                }
            }
        }

    AbsentExportScreenContent(
        items = items.toImmutableList(),
        selectedItems = selectedItems.toImmutableList(),
        selectedEmployees = selectedEmployee,
        showSearchBar = showSearchBar,
        searchText = searchText,
        onClearClick = viewModel::clearSearchText,
        onSearchTextChanged = viewModel::searchTextChanged,
        onClickOpenSearch = viewModel::openSearchBar,
        onClickCloseSearch = viewModel::closeSearchBar,
        onClickSelectAll = viewModel::selectAllItems,
        onClickDeselect = viewModel::deselectItems,
        onSelectItem = viewModel::selectItem,
        onSelectEmployee = viewModel::selectEmployee,
        onClickExport = {
            scope.launch {
                val result = ImportExport.createFile(
                    context = context,
                    fileName = EXPORT_ABSENT_FILE_NAME,
                )
                exportLauncher.launch(result)
                viewModel.onEvent(AbsentSettingsEvent.GetExportedItems)
            }
        },
        onBackClick = navigator::navigateUp,
        onClickToAddItem = {
            navigator.navigate(AddEditAbsentScreenDestination())
        },
        modifier = Modifier,
    )
}

@VisibleForTesting
@Composable
internal fun AbsentExportScreenContent(
    items: ImmutableList<EmployeeWithAbsents>,
    selectedItems: ImmutableList<Int>,
    selectedEmployees: List<Int>,
    showSearchBar: Boolean,
    searchText: String,
    onClearClick: () -> Unit,
    onSearchTextChanged: (String) -> Unit,
    onClickOpenSearch: () -> Unit,
    onClickCloseSearch: () -> Unit,
    onClickSelectAll: () -> Unit,
    onClickDeselect: () -> Unit,
    onSelectItem: (Int) -> Unit,
    onSelectEmployee: (Int) -> Unit,
    onClickExport: () -> Unit,
    onBackClick: () -> Unit,
    onClickToAddItem: () -> Unit,
    modifier: Modifier = Modifier,
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
) {
    TrackScreenViewEvent(screenName = "AbsentExportScreen")

    val text = if (searchText.isEmpty()) ABSENT_NOT_AVAILABLE else Constants.SEARCH_ITEM_NOT_FOUND
    val title =
        if (selectedItems.isEmpty()) EXPORT_ABSENT_TITLE else "${selectedItems.size} Selected"

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            onClickDeselect()
        } else if (showSearchBar) {
            onClickCloseSearch()
        } else {
            onBackClick()
        }
    }

    ExportScaffold(
        title = title,
        exportNote = createExportNote(selectedItems, items.size, "absent"),
        searchPlaceholder = ABSENT_SEARCH_PLACEHOLDER,
        exportButtonText = EXPORT_ABSENT_TITLE,
        emptyButtonText = CREATE_NEW_ABSENT,
        emptyText = text,
        showBottomBar = items.isNotEmpty(),
        showBackButton = selectedItems.isEmpty(),
        searchText = searchText,
        showSearchBar = showSearchBar,
        showScrollToTop = !lazyListState.isScrollingUp(),
        onBackClick = onBackClick,
        onClickDeselect = onClickDeselect,
        onClickSelectAll = onClickSelectAll,
        onClickOpenSearch = onClickOpenSearch,
        onClickCloseSearch = onClickCloseSearch,
        onClearClick = onClearClick,
        onSearchTextChanged = onSearchTextChanged,
        onClickExport = onClickExport,
        onClickEmptyBtn = onClickToAddItem,
        onClickScrollToTop = {
            scope.launch {
                lazyListState.animateScrollToItem(0)
            }
        },
        modifier = modifier,
    ) {
        AbsentEmployeeList(
            items = items,
            expanded = selectedEmployees::contains,
            onExpandChanged = onSelectEmployee,
            doesSelected = selectedItems::contains,
            onClick = onSelectItem,
            onLongClick = onSelectItem,
            modifier = Modifier,
            onChipClick = {},
            lazyListState = lazyListState,
        )
    }
}

@DevicePreviews
@Composable
private fun AbsentExportScreenEmptyDataPreview() {
    PoposRoomTheme {
        AbsentExportScreenContent(
            items = persistentListOf(),
            selectedItems = persistentListOf(),
            selectedEmployees = listOf(),
            showSearchBar = false,
            searchText = "",
            onClearClick = {},
            onSearchTextChanged = {},
            onClickOpenSearch = {},
            onClickCloseSearch = {},
            onClickSelectAll = {},
            onClickDeselect = {},
            onSelectItem = {},
            onSelectEmployee = {},
            onClickExport = {},
            onBackClick = {},
            onClickToAddItem = {},
            modifier = Modifier,
        )
    }
}

@DevicePreviews
@Composable
private fun AbsentExportScreenContentPreview(
    items: ImmutableList<EmployeeWithAbsents> = employeesWithAbsents.toImmutableList(),
) {
    PoposRoomTheme {
        AbsentExportScreenContent(
            items = items,
            selectedItems = persistentListOf(),
            selectedEmployees = listOf(1, 2, 3),
            showSearchBar = false,
            searchText = "",
            onClearClick = {},
            onSearchTextChanged = {},
            onClickOpenSearch = {},
            onClickCloseSearch = {},
            onClickSelectAll = {},
            onClickDeselect = {},
            onSelectItem = {},
            onSelectEmployee = {},
            onClickExport = {},
            onBackClick = {},
            onClickToAddItem = {},
            modifier = Modifier,
        )
    }
}
