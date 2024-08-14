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

package com.niyaj.expenses.settings

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.ExpenseTestTags.IMPORT_EXPENSE_NOTE_TEXT
import com.niyaj.common.tags.ExpenseTestTags.IMPORT_EXPENSE_TITLE
import com.niyaj.common.utils.createImportNote
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.domain.utils.ImportExport
import com.niyaj.expenses.components.ExpensesList
import com.niyaj.model.Expense
import com.niyaj.ui.components.ImportScaffold
import com.niyaj.ui.parameterProvider.ExpensePreviewData
import com.niyaj.ui.utils.DevicePreviews
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Destination
@Composable
fun ExpensesImportScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: ExpensesSettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val importedItems by viewModel.importedItems.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)
    val selectedItems = viewModel.selectedItems.toList()

    var importJob: Job? = null

    val importLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            it.data?.data?.let {
                importJob?.cancel()

                importJob = scope.launch {
                    val data = ImportExport.readDataAsync<Expense>(context, it)

                    viewModel.onEvent(ExpensesSettingsEvent.OnImportExpensesItemsFromFile(data))
                }
            }
        }

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

    ExpensesImportScreenContent(
        modifier = Modifier,
        importedItems = importedItems.toImmutableList(),
        selectedItems = selectedItems.toImmutableList(),
        isLoading = isLoading,
        onClickSelectItem = viewModel::selectItem,
        onClickSelectAll = viewModel::selectAllItems,
        onClickDeselect = viewModel::deselectItems,
        onClickImport = {
            viewModel.onEvent(ExpensesSettingsEvent.ImportExpensesItemsToDatabase)
        },
        onClickOpenFile = {
            importLauncher.launch(ImportExport.openFile(context))
        },
        onBackClick = navigator::navigateUp,
    )
}

@VisibleForTesting
@Composable
internal fun ExpensesImportScreenContent(
    modifier: Modifier = Modifier,
    importedItems: ImmutableList<Expense>,
    selectedItems: ImmutableList<Int>,
    isLoading: Boolean,
    onClickSelectItem: (Int) -> Unit,
    onClickSelectAll: () -> Unit,
    onClickDeselect: () -> Unit,
    onClickImport: () -> Unit,
    onClickOpenFile: () -> Unit,
    onBackClick: () -> Unit,
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    TrackScreenViewEvent(screenName = "Expenses Import Screen")
    val title =
        if (selectedItems.isEmpty()) IMPORT_EXPENSE_TITLE else "${selectedItems.size} Selected"

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            onClickDeselect()
        } else {
            onBackClick()
        }
    }

    ImportScaffold(
        title = title,
        modifier = modifier,
        isLoading = isLoading,
        importFileNote = IMPORT_EXPENSE_NOTE_TEXT,
        importButtonText = IMPORT_EXPENSE_TITLE,
        importNote = createImportNote(selectedItems, importedItems.size, "expense"),
        showBottomBar = importedItems.isNotEmpty(),
        showBackButton = selectedItems.isEmpty(),
        showScrollToTop = !lazyListState.isScrollingUp(),
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onClickDeselect = onClickDeselect,
        onClickSelectAll = onClickSelectAll,
        onClickImport = onClickImport,
        onClickOpenFile = onClickOpenFile,
        onClickScrollToTop = {
            scope.launch {
                lazyListState.animateScrollToItem(index = 0)
            }
        },
    ) {
        ExpensesList(
            items = importedItems,
            doesSelected = selectedItems::contains,
            onSelectItem = onClickSelectItem,
            modifier = Modifier,
            lazyListState = lazyListState,
        )
    }
}

@DevicePreviews
@Composable
private fun ExpensesImportScreenEmptyContentPreview() {
    PoposRoomTheme {
        ExpensesImportScreenContent(
            modifier = Modifier,
            importedItems = persistentListOf(),
            selectedItems = persistentListOf(),
            isLoading = false,
            onClickSelectItem = {},
            onClickSelectAll = {},
            onClickDeselect = {},
            onClickImport = {},
            onClickOpenFile = {},
            onBackClick = {},
        )
    }
}

@DevicePreviews
@Composable
private fun ExpensesImportScreenContentPreview(
    items: ImmutableList<Expense> = ExpensePreviewData.expenses.toImmutableList(),
) {
    PoposRoomTheme {
        ExpensesImportScreenContent(
            modifier = Modifier,
            importedItems = items,
            selectedItems = persistentListOf(),
            isLoading = false,
            onClickSelectItem = {},
            onClickSelectAll = {},
            onClickDeselect = {},
            onClickImport = {},
            onClickOpenFile = {},
            onBackClick = {},
        )
    }
}
