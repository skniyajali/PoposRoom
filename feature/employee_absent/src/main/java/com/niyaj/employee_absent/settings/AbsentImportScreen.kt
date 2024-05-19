package com.niyaj.employee_absent.settings

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.common.tags.AbsentScreenTags.IMPORT_ABSENT_BTN_TEXT
import com.niyaj.common.tags.AbsentScreenTags.IMPORT_ABSENT_NOTE_TEXT
import com.niyaj.common.tags.AbsentScreenTags.IMPORT_ABSENT_OPN_FILE
import com.niyaj.common.tags.AbsentScreenTags.IMPORT_ABSENT_TITLE
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.domain.utils.ImportExport
import com.niyaj.employee_absent.AbsentData
import com.niyaj.employee_absent.destinations.AddEditAbsentScreenDestination
import com.niyaj.model.EmployeeWithAbsents
import com.niyaj.ui.components.EmptyImportScreen
import com.niyaj.ui.components.InfoText
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

@Destination
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AbsentImportScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: AbsentSettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    val selectedEmployee = viewModel.selectedEmployee.collectAsStateWithLifecycle().value
    val importedItems = viewModel.importedItems.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()
    var importJob: Job? = null

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
                    val data = ImportExport.readDataAsync<EmployeeWithAbsents>(context, it)

                    viewModel.onEvent(AbsentSettingsEvent.OnImportAbsentItemsFromFile(data))
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

    TrackScreenViewEvent(screenName = "Absent Import Screen")

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        } else {
            navigator.navigateUp()
        }
    }

    StandardScaffoldRouteNew(
        title = if (selectedItems.isEmpty()) IMPORT_ABSENT_TITLE else "${selectedItems.size} Selected",
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
                        .testTag(IMPORT_ABSENT_BTN_TEXT),
                    enabled = true,
                    text = IMPORT_ABSENT_BTN_TEXT,
                    icon = PoposIcons.Download,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                    onClick = {
                        scope.launch {
                            viewModel.onEvent(AbsentSettingsEvent.ImportAbsentItemsToDatabase)
                        }
                    },
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
    ) { paddingValues ->
        Crossfade(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            targetState = importedItems.isEmpty(),
            label = "Imported Items",
        ) { itemNotAvailable ->
            if (itemNotAvailable) {
                EmptyImportScreen(
                    text = IMPORT_ABSENT_NOTE_TEXT,
                    buttonText = IMPORT_ABSENT_OPN_FILE,
                    icon = PoposIcons.FileOpen,
                    onClick = {
                        scope.launch {
                            askForPermissions()
                            val result = ImportExport.openFile(context)
                            importLauncher.launch(result)
                        }
                    },
                )
            } else {
                TrackScrollJank(
                    scrollableState = lazyListState,
                    stateName = "Imported Absentees::List",
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(SpaceSmall),
                    state = lazyListState,
                ) {
                    items(
                        items = importedItems,
                        key = { it.employee.employeeId },
                    ) { item ->
                        if (item.absents.isNotEmpty()) {
                            AbsentData(
                                item = item,
                                expanded = {
                                    selectedEmployee == it
                                },
                                doesSelected = {
                                    selectedItems.contains(it)
                                },
                                onClick = viewModel::selectItem,
                                onExpandChanged = viewModel::selectEmployee,
                                onLongClick = viewModel::selectItem,
                                onChipClick = {
                                    navigator.navigate(AddEditAbsentScreenDestination(employeeId = it))
                                },
                                showTrailingIcon = false,
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }
                    }
                }
            }
        }
    }
}