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

package com.niyaj.addonitem.settings

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import com.niyaj.addonitem.components.AddOnItemData
import com.niyaj.common.tags.AddOnTestTags.IMPORT_ADDON_NOTE_TEXT
import com.niyaj.common.tags.AddOnTestTags.IMPORT_ADDON_TITLE
import com.niyaj.common.utils.createImportNote
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.domain.utils.ImportExport
import com.niyaj.model.AddOnItem
import com.niyaj.ui.components.ImportScaffold
import com.niyaj.ui.parameterProvider.AddOnPreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens.ADD_ON_IMPORT_SCREEN
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
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

@Destination(route = ADD_ON_IMPORT_SCREEN)
@Composable
fun AddOnImportScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: AddOnSettingsViewModel = hiltViewModel(),
) {
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val importedItems by viewModel.importedItems.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val selectedItems = viewModel.selectedItems.toList()

    var importJob: Job? = null

    val importLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            result.data?.data?.let {
                importJob?.cancel()

                importJob = scope.launch {
                    ImportExport.readFile<AddOnItem>(context, it).onSuccess {
                        viewModel.onEvent(AddOnSettingsEvent.OnImportAddOnItemsFromFile(it))
                    }.onFailure {
                        snackbarHostState.showSnackbar(it.message.toString())
                    }
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

    AddOnImportScreenContent(
        importedItems = importedItems.toImmutableList(),
        selectedItems = selectedItems.toImmutableList(),
        isLoading = isLoading,
        snackbarHostState = snackbarHostState,
        onClickSelectItem = viewModel::selectItem,
        onClickSelectAll = viewModel::selectAllItems,
        onClickDeselect = viewModel::deselectItems,
        onClickImport = {
            viewModel.onEvent(AddOnSettingsEvent.ImportAddOnItemsToDatabase)
        },
        onClickOpenFile = {
            scope.launch {
                importLauncher.launch(ImportExport.openFile(context))
            }
        },
        onBackClick = navigator::navigateUp,
        modifier = Modifier,
    )
}

@VisibleForTesting
@Composable
internal fun AddOnImportScreenContent(
    importedItems: ImmutableList<AddOnItem>,
    selectedItems: ImmutableList<Int>,
    isLoading: Boolean,
    onClickSelectItem: (Int) -> Unit,
    onClickSelectAll: () -> Unit,
    onClickDeselect: () -> Unit,
    onClickImport: () -> Unit,
    onClickOpenFile: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    TrackScreenViewEvent(screenName = ADD_ON_IMPORT_SCREEN)

    val title =
        if (selectedItems.isEmpty()) IMPORT_ADDON_TITLE else "${selectedItems.size} Selected"

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
        importFileNote = IMPORT_ADDON_NOTE_TEXT,
        importNote = createImportNote(selectedItems, importedItems.size, "addon"),
        importButtonText = IMPORT_ADDON_TITLE,
        showBottomBar = importedItems.isNotEmpty(),
        showBackButton = selectedItems.isEmpty(),
        showScrollToTop = !lazyGridState.isScrollingUp(),
        isLoading = isLoading,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onClickDeselect = onClickDeselect,
        onClickSelectAll = onClickSelectAll,
        onClickImport = onClickImport,
        onClickOpenFile = onClickOpenFile,
        onClickScrollToTop = {
            scope.launch {
                lazyGridState.animateScrollToItem(index = 0)
            }
        },
    ) {
        TrackScrollJank(scrollableState = lazyGridState, stateName = "addon-import:list")

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(SpaceSmall),
            columns = GridCells.Fixed(2),
            state = lazyGridState,
        ) {
            items(
                items = importedItems,
                key = { it.itemId },
            ) { item: AddOnItem ->
                AddOnItemData(
                    item = item,
                    doesSelected = {
                        selectedItems.contains(it)
                    },
                    onClick = onClickSelectItem,
                    onLongClick = onClickSelectItem,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun AddOnImportScreenEmptyContentPreview() {
    PoposRoomTheme {
        AddOnImportScreenContent(
            importedItems = persistentListOf(),
            selectedItems = persistentListOf(),
            isLoading = false,
            onClickSelectItem = {},
            onClickSelectAll = {},
            onClickDeselect = {},
            onClickImport = {},
            onClickOpenFile = {},
            onBackClick = {},
            modifier = Modifier,
        )
    }
}

@DevicePreviews
@Composable
private fun AddOnImportScreenContentPreview(
    items: ImmutableList<AddOnItem> = AddOnPreviewData.addOnItemList.toImmutableList(),
) {
    PoposRoomTheme {
        AddOnImportScreenContent(
            importedItems = items,
            selectedItems = persistentListOf(),
            isLoading = false,
            onClickSelectItem = {},
            onClickSelectAll = {},
            onClickDeselect = {},
            onClickImport = {},
            onClickOpenFile = {},
            onBackClick = {},
            modifier = Modifier,
        )
    }
}
