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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.addonitem.components.AddOnItemData
import com.niyaj.common.tags.AddOnTestTags.IMPORT_ADDON_BTN_TEXT
import com.niyaj.common.tags.AddOnTestTags.IMPORT_ADDON_NOTE_TEXT
import com.niyaj.common.tags.AddOnTestTags.IMPORT_ADDON_OPN_FILE
import com.niyaj.common.tags.AddOnTestTags.IMPORT_ADDON_TITLE
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceLarge
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.domain.utils.ImportExport
import com.niyaj.model.AddOnItem
import com.niyaj.ui.components.EmptyImportScreen
import com.niyaj.ui.components.InfoText
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.ScrollToTop
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Destination(route = ADD_ON_IMPORT_SCREEN)
@Composable
fun AddOnImportScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: AddOnSettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val importedItems by viewModel.importedItems.collectAsStateWithLifecycle()
    val selectedItems = viewModel.selectedItems.toList()

    var importJob: Job? = null

    val importLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            it.data?.data?.let {
                importJob?.cancel()

                importJob = scope.launch {
                    val data = ImportExport.readData<AddOnItem>(context, it)

                    viewModel.onEvent(AddOnSettingsEvent.OnImportAddOnItemsFromFile(data))
                }
            }
        }

    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

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

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        } else {
            navigator.navigateUp()
        }
    }

    TrackScreenViewEvent(screenName = ADD_ON_IMPORT_SCREEN)

    AddOnImportScreenContent(
        modifier = Modifier,
        importedItems = importedItems.toImmutableList(),
        selectedItems = selectedItems.toImmutableList(),
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
    )
}

@VisibleForTesting
@Composable
internal fun AddOnImportScreenContent(
    modifier: Modifier = Modifier,
    importedItems: ImmutableList<AddOnItem>,
    selectedItems: ImmutableList<Int>,
    onClickSelectItem: (Int) -> Unit,
    onClickSelectAll: () -> Unit,
    onClickDeselect: () -> Unit,
    onClickImport: () -> Unit,
    onClickOpenFile: () -> Unit,
    onBackClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    val title =
        if (selectedItems.isEmpty()) IMPORT_ADDON_TITLE else "${selectedItems.size} Selected"

    PoposSecondaryScaffold(
        modifier = modifier,
        title = title,
        showBackButton = selectedItems.isEmpty(),
        showBottomBar = importedItems.isNotEmpty(),
        showSecondaryBottomBar = true,
        navActions = {
            AnimatedVisibility(
                visible = importedItems.isNotEmpty(),
            ) {
                IconButton(
                    onClick = onClickSelectAll,
                ) {
                    Icon(
                        imageVector = PoposIcons.Checklist,
                        contentDescription = Constants.SELECT_ALL_ICON,
                    )
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpaceMedium, vertical = SpaceLarge),
                verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            ) {
                InfoText(text = "${if (selectedItems.isEmpty()) "All" else "${selectedItems.size}"} addon item will be imported.")

                PoposButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(IMPORT_ADDON_BTN_TEXT),
                    enabled = true,
                    text = IMPORT_ADDON_BTN_TEXT,
                    icon = PoposIcons.Download,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                    onClick = onClickImport,
                )
            }
        },
        fabPosition = FabPosition.End,
        floatingActionButton = {
            ScrollToTop(
                visible = !lazyGridState.isScrollingUp(),
                onClick = {
                    scope.launch {
                        lazyGridState.animateScrollToItem(index = 0)
                    }
                },
            )
        },
        onBackClick = onBackClick,
        navigationIcon = {
            IconButton(
                onClick = onClickDeselect,
            ) {
                Icon(
                    imageVector = PoposIcons.Close,
                    contentDescription = "Deselect All",
                )
            }
        },
    ) { paddingValues ->
        Crossfade(
            targetState = importedItems.isEmpty(),
            label = "Imported Items",
            modifier = Modifier.padding(paddingValues),
        ) { itemAvailable ->
            if (itemAvailable) {
                EmptyImportScreen(
                    text = IMPORT_ADDON_NOTE_TEXT,
                    buttonText = IMPORT_ADDON_OPN_FILE,
                    icon = PoposIcons.FileOpen,
                    onClick = onClickOpenFile,
                )
            } else {
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
    }
}

@DevicePreviews
@Composable
private fun AddOnImportScreenEmptyContentPreview() {
    PoposRoomTheme {
        AddOnImportScreenContent(
            modifier = Modifier,
            importedItems = persistentListOf(),
            selectedItems = persistentListOf(),
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
private fun AddOnImportScreenContentPreview(
    items: ImmutableList<AddOnItem> = AddOnPreviewData.addOnItemList.toImmutableList(),
) {
    PoposRoomTheme {
        AddOnImportScreenContent(
            modifier = Modifier,
            importedItems = items,
            selectedItems = persistentListOf(),
            onClickSelectItem = {},
            onClickSelectAll = {},
            onClickDeselect = {},
            onClickImport = {},
            onClickOpenFile = {},
            onBackClick = {},
        )
    }
}
