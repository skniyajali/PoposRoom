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

package com.niyaj.market.measure_unit.settings

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.common.tags.MeasureUnitTestTags.IMPORT_UNIT_BTN_TEXT
import com.niyaj.common.tags.MeasureUnitTestTags.IMPORT_UNIT_NOTE_TEXT
import com.niyaj.common.tags.MeasureUnitTestTags.IMPORT_UNIT_OPN_FILE
import com.niyaj.common.tags.MeasureUnitTestTags.IMPORT_UNIT_TITLE
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.domain.utils.ImportExport
import com.niyaj.market.components.MeasureUnitItem
import com.niyaj.model.MeasureUnit
import com.niyaj.ui.components.EmptyImportScreen
import com.niyaj.ui.components.InfoText
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardScaffoldRouteNew
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@Destination
fun ImportMeasureUnitScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: MeasureUnitSettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()

    val importedItems = viewModel.importedItems.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()
    var importJob: Job? = null
    val isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value

    val hasStoragePermission = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ),
    )

    val askForPermissions = {
        if (!hasStoragePermission.allPermissionsGranted) {
            hasStoragePermission.launchMultiplePermissionRequest()
        }
    }

    val importLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            it.data?.data?.let {
                importJob?.cancel()

                importJob = scope.launch {
                    val data = ImportExport.readDataAsync<MeasureUnit>(context, it)

                    viewModel.onEvent(MeasureUnitSettingsEvent.OnImportItemsFromFile(data))
                }
            }
        }

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

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

    TrackScreenViewEvent(screenName = IMPORT_UNIT_TITLE)

    StandardScaffoldRouteNew(
        title = if (selectedItems.isEmpty()) IMPORT_UNIT_TITLE else "${selectedItems.size} Selected",
        showBackButton = selectedItems.isEmpty(),
        showBottomBar = importedItems.isNotEmpty(),
        navActions = {
            AnimatedVisibility(
                visible = importedItems.isNotEmpty(),
            ) {
                IconButton(
                    onClick = viewModel::selectAllItems,
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
                    .padding(SpaceSmallMax),
                verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            ) {
                InfoText(text = "${if (selectedItems.isEmpty()) "All" else "${selectedItems.size}"} item will be imported.")

                StandardButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(IMPORT_UNIT_BTN_TEXT),
                    enabled = true,
                    text = IMPORT_UNIT_BTN_TEXT,
                    icon = PoposIcons.Download,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                    onClick = {
                        scope.launch {
                            viewModel.onEvent(MeasureUnitSettingsEvent.ImportItemsToDatabase)
                        }
                    },
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
        navigationIcon = {
            IconButton(
                onClick = viewModel::deselectItems,
            ) {
                Icon(
                    imageVector = PoposIcons.Close,
                    contentDescription = "Deselect All",
                )
            }
        },
        onBackClick = navigator::navigateUp,
    ) {
        Crossfade(
            targetState = importedItems.isEmpty(),
            label = "Imported Products",
        ) { categoriesAvailable ->
            if (categoriesAvailable) {
                EmptyImportScreen(
                    text = IMPORT_UNIT_NOTE_TEXT,
                    buttonText = IMPORT_UNIT_OPN_FILE,
                    icon = PoposIcons.FileOpen,
                    onClick = {
                        scope.launch {
                            askForPermissions()
                            val result = ImportExport.openFile(context)
                            importLauncher.launch(result)
                        }
                    },
                )
            } else if (isLoading) {
                LoadingIndicator()
            } else {
                TrackScrollJank(scrollableState = lazyGridState, stateName = "ImportUnitList::State")

                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    contentPadding = PaddingValues(SpaceSmall),
                    columns = GridCells.Fixed(2),
                    state = lazyGridState,
                ) {
                    items(
                        items = importedItems,
                        key = { it.unitId },
                    ) { item: MeasureUnit ->
                        MeasureUnitItem(
                            item = item,
                            doesSelected = selectedItems::contains,
                            onClick = viewModel::selectItem,
                            onLongClick = viewModel::selectItem,
                        )
                    }
                }
            }
        }
    }
}