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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_SETTINGS_NOTE
import com.niyaj.common.tags.AbsentScreenTags.IMPORT_ABSENT_NOTE_TEXT
import com.niyaj.common.tags.AbsentScreenTags.IMPORT_ABSENT_OPN_FILE
import com.niyaj.common.tags.AbsentScreenTags.IMPORT_ABSENT_TITLE
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceLarge
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.domain.utils.ImportExport
import com.niyaj.employeeAbsent.components.AbsentEmployeeList
import com.niyaj.model.EmployeeWithAbsents
import com.niyaj.ui.components.EmptyImportScreen
import com.niyaj.ui.components.InfoText
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.parameterProvider.AbsentPreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens.ABSENT_IMPORT_SCREEN
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

@Destination(route = ABSENT_IMPORT_SCREEN)
@Composable
fun AbsentImportScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: AbsentSettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val importedItems = viewModel.importedItems.collectAsStateWithLifecycle().value
    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    val selectedEmployees = viewModel.selectedEmployee.toList()
    val selectedItems = viewModel.selectedItems.toList()

    var importJob: Job? = null

    val importLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            it.data?.data?.let {
                importJob?.cancel()

                importJob = scope.launch {
                    val data = ImportExport.readDataAsync<EmployeeWithAbsents>(context, it)

                    viewModel.onEvent(AbsentSettingsEvent.OnImportAbsentItemsFromFile(data))
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

    AbsentImportScreenContent(
        modifier = Modifier,
        importedItems = importedItems.toImmutableList(),
        selectedItems = selectedItems.toImmutableList(),
        selectedEmployees = selectedEmployees,
        onClickSelectItem = viewModel::selectItem,
        onClickSelectAll = viewModel::selectAllItems,
        onClickDeselect = viewModel::deselectItems,
        onSelectEmployee = viewModel::selectEmployee,
        onClickImport = {
            viewModel.onEvent(AbsentSettingsEvent.ImportAbsentItemsToDatabase)
        },
        onClickOpenFile = {
            importLauncher.launch(ImportExport.openFile(context))
        },
        onBackClick = navigator::navigateUp,
    )
}

@VisibleForTesting
@Composable
internal fun AbsentImportScreenContent(
    modifier: Modifier = Modifier,
    importedItems: ImmutableList<EmployeeWithAbsents>,
    selectedItems: ImmutableList<Int>,
    selectedEmployees: List<Int>,
    onClickSelectItem: (Int) -> Unit,
    onClickSelectAll: () -> Unit,
    onClickDeselect: () -> Unit,
    onSelectEmployee: (Int) -> Unit,
    onClickImport: () -> Unit,
    onClickOpenFile: () -> Unit,
    onBackClick: () -> Unit,
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
    padding: PaddingValues = PaddingValues(SpaceSmallMax, 0.dp, SpaceSmallMax, SpaceLarge),
) {
    TrackScreenViewEvent(screenName = ABSENT_IMPORT_SCREEN)

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            onClickDeselect()
        } else {
            onBackClick()
        }
    }

    PoposSecondaryScaffold(
        modifier = modifier,
        title = if (selectedItems.isEmpty()) IMPORT_ABSENT_TITLE else "${selectedItems.size} Selected",
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
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            ) {
                InfoText(text = "${if (selectedItems.isEmpty()) "All" else "${selectedItems.size}"} absentees item will be imported.")

                PoposButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(IMPORT_ABSENT_TITLE),
                    enabled = true,
                    text = IMPORT_ABSENT_TITLE,
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
                visible = !lazyListState.isScrollingUp(),
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
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
                    text = IMPORT_ABSENT_NOTE_TEXT,
                    buttonText = IMPORT_ABSENT_OPN_FILE,
                    note = ABSENT_SETTINGS_NOTE,
                    icon = PoposIcons.FileOpen,
                    onClick = onClickOpenFile,
                )
            } else {
                AbsentEmployeeList(
                    modifier = Modifier,
                    items = importedItems,
                    expanded = selectedEmployees::contains,
                    onExpandChanged = onSelectEmployee,
                    doesSelected = selectedItems::contains,
                    onClick = onClickSelectItem,
                    onLongClick = onClickSelectItem,
                    onChipClick = {},
                    lazyListState = lazyListState
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun AbsentImportScreenEmptyContentPreview() {
    PoposRoomTheme {
        AbsentImportScreenContent(
            modifier = Modifier,
            importedItems = persistentListOf(),
            selectedItems = persistentListOf(),
            selectedEmployees = listOf(),
            onClickSelectItem = {},
            onClickSelectAll = {},
            onClickDeselect = {},
            onSelectEmployee = {},
            onClickImport = {},
            onClickOpenFile = {},
            onBackClick = {},
        )
    }
}

@DevicePreviews
@Composable
private fun AbsentImportScreenContentPreview(
    items: ImmutableList<EmployeeWithAbsents> = AbsentPreviewData.employeesWithAbsents.toImmutableList(),
) {
    PoposRoomTheme {
        AbsentImportScreenContent(
            modifier = Modifier,
            importedItems = items,
            selectedItems = persistentListOf(),
            selectedEmployees = listOf(1, 2, 3),
            onClickSelectItem = {},
            onClickSelectAll = {},
            onClickDeselect = {},
            onSelectEmployee = {},
            onClickImport = {},
            onClickOpenFile = {},
            onBackClick = {},
        )
    }
}

