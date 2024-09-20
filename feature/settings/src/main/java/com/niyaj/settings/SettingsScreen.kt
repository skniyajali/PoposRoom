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

package com.niyaj.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.utils.shareContent
import com.niyaj.common.utils.toBarDate
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.settings.destinations.DataDeletionSettingsScreenDestination
import com.niyaj.ui.components.LoadingIndicatorHalf
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrollingUp
import com.niyaj.ui.utils.navigate
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination(route = Screens.SETTINGS_SCREEN)
@Composable
fun SettingsScreen(
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<DataDeletionSettingsScreenDestination, String>,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { viewModel.restoreBackup(uri) }
        },
    )

    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip"),
        onResult = {
            if (it != null && viewModel.cacheBackupUri != null) {
                viewModel.copyBackupFile(viewModel.cacheBackupUri!!, it)
            }
        },
    )

    LaunchedEffect(
        key1 = true,
    ) {
        viewModel.eventFlow.collectLatest {
            when (it) {
                is UiEvent.OnError -> {
                    snackbarHostState.showSnackbar(it.errorMessage)
                }

                is UiEvent.OnSuccess -> {
                    val label = if (it.successMessage.contains("Backup")) "Share" else null
                    val action = snackbarHostState.showSnackbar(
                        message = it.successMessage,
                        actionLabel = label,
                    )

                    if (viewModel.cacheBackupUri != null && action == SnackbarResult.ActionPerformed) {
                        context.shareContent(viewModel.cacheBackupUri!!, type = "application/zip")
                    }
                }
            }
        }
    }

    resultRecipient.onNavResult {
        when (it) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarHostState.showSnackbar(it.value)
                }
            }
        }
    }

    SettingsScreenContent(
        snackbarState = snackbarHostState,
        isLoading = isLoading,
        modifier = modifier,
        onClickChangeAppearance = {
            navigator.navigate(Screens.APP_SETTINGS_DIALOG)
        },
        onClickDeletionSettings = {
            navigator.navigate(Screens.DATA_DELETION_SETTINGS_SCREEN)
        },
        onClickBackupDb = {
            viewModel.createBackup {
                backupLauncher.launch("PoposBackup-${System.currentTimeMillis().toBarDate}.zip")
            }
        },
        onClickRestoreDb = {
            restoreLauncher.launch("application/zip")
        },
        onClickDeleteAll = viewModel::deleteAllData,
        onBackClick = {
            navigator.popBackStack()
        },
    )
}

@VisibleForTesting
@Composable
internal fun SettingsScreenContent(
    isLoading: Boolean,
    onClickChangeAppearance: () -> Unit,
    onClickDeletionSettings: () -> Unit,
    onClickBackupDb: () -> Unit,
    onClickRestoreDb: () -> Unit,
    onClickDeleteAll: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarState: SnackbarHostState = remember { SnackbarHostState() },
    lazyListState: LazyListState = rememberLazyListState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    containerColor: Color = MaterialTheme.colorScheme.background,
) {
    val openDialog = remember { mutableStateOf(false) }

    TrackScreenViewEvent(screenName = Screens.SETTINGS_SCREEN)

    PoposSecondaryScaffold(
        title = "App Settings",
        onBackClick = onBackClick,
        modifier = modifier,
        showBackButton = true,
        showBottomBar = false,
        fabPosition = FabPosition.End,
        snackbarHostState = snackbarState,
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
    ) { paddingValues ->
        TrackScrollJank(
            scrollableState = lazyListState,
            stateName = "App Settings::Columns",
        )

        Crossfade(
            targetState = isLoading,
            label = "",
        ) {
            if (it) {
                LoadingIndicatorHalf()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(SpaceSmall),
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                ) {
                    item("Change Appearance") {
                        SettingsCard(
                            title = "Change Theme",
                            subtitle = "Click here to change the appearance of the app",
                            icon = PoposIcons.Settings,
                            onClick = onClickChangeAppearance,
                            containerColor = containerColor,
                        )
                    }

                    item("Data Deletion Settings") {
                        SettingsCard(
                            title = "Data Deletion Settings",
                            subtitle = "Click here to change the data deletion settings",
                            icon = PoposIcons.AutoDelete,
                            onClick = onClickDeletionSettings,
                            containerColor = containerColor,
                        )
                    }

                    item("Delete All Data") {
                        SettingsCard(
                            title = "Delete All Data",
                            subtitle = "Click here to delete all data from database",
                            icon = PoposIcons.DeleteSweep,
                            onClick = {
                                openDialog.value = true
                            },
                            containerColor = containerColor,
                        )
                    }

                    item("Backup Database") {
                        SettingsCard(
                            title = "Backup Database",
                            subtitle = "Click here to backup database to file.",
                            icon = PoposIcons.Upload,
                            onClick = onClickBackupDb,
                            containerColor = containerColor,
                        )
                    }

                    item("Restore Database") {
                        SettingsCard(
                            title = "Restore Database",
                            subtitle = "Click here to restore database from file.",
                            icon = PoposIcons.Import,
                            onClick = onClickRestoreDb,
                            containerColor = containerColor,
                        )
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = openDialog.value,
    ) {
        StandardDialog(
            title = "Delete All Data",
            message = "Are you sure you want to delete all data including today?",
            onConfirm = {
                openDialog.value = false
                onClickDeleteAll()
            },
            onDismiss = {
                openDialog.value = false
            },
        )
    }
}

@DevicePreviews
@Composable
private fun SettingsScreenContentPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        SettingsScreenContent(
            modifier = modifier,
            isLoading = false,
            onClickChangeAppearance = {},
            onClickDeletionSettings = {},
            onClickBackupDb = {},
            onClickRestoreDb = {},
            onClickDeleteAll = {},
            onBackClick = {},
        )
    }
}
