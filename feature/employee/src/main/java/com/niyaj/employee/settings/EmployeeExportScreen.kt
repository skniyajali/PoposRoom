package com.niyaj.employee.settings

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upload
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
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.common.tags.EmployeeTestTags
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.EmployeeTestTags.EXPORT_EMPLOYEE_BTN
import com.niyaj.common.tags.EmployeeTestTags.EXPORT_EMPLOYEE_BTN_TEXT
import com.niyaj.common.tags.EmployeeTestTags.EXPORT_EMPLOYEE_FILE_NAME
import com.niyaj.common.tags.EmployeeTestTags.EXPORT_EMPLOYEE_TITLE
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.employee.EmployeeData
import com.niyaj.employee.destinations.AddEditEmployeeScreenDestination
import com.niyaj.model.Employee
import com.niyaj.ui.components.InfoText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.components.StandardSearchBar
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrollingUp
import com.niyaj.domain.utils.ImportExport
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.launch

@Destination
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EmployeeExportScreen(
    navController: NavController,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: EmployeeSettingsViewModel = hiltViewModel(),
) {

    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    val employees = viewModel.employees.collectAsStateWithLifecycle().value
    val exportedItems = viewModel.exportedItems.collectAsStateWithLifecycle().value

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    val selectedItems = viewModel.selectedItems.toList()

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

    val context = LocalContext.current

    val hasStoragePermission = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    )

    val askForPermissions = {
        if (!hasStoragePermission.allPermissionsGranted) {
            hasStoragePermission.launchMultiplePermissionRequest()
        }
    }

    val exportLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            it.data?.data?.let {
                scope.launch {
                    val result = ImportExport.writeData(context, it, exportedItems)

                    if (result) {
                        resultBackNavigator.navigateBack("${exportedItems.size} Employees has been exported.")
                    } else {
                        resultBackNavigator.navigateBack("Unable to export employees.")
                    }
                }
            }
        }

    fun onBackClick() {
        if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        } else if (showSearchBar) {
            viewModel.closeSearchBar()
        } else {
            navController.navigateUp()
        }
    }

    BackHandler {
        onBackClick()
    }

    TrackScreenViewEvent(screenName = "Employee Export Screen")
    
    StandardScaffoldNew(
        navController = navController,
        title = if (selectedItems.isEmpty()) EXPORT_EMPLOYEE_TITLE else "${selectedItems.size} Selected",
        showBackButton = true,
        showBottomBar = employees.isNotEmpty(),
        navActions = {
            if (showSearchBar) {
                StandardSearchBar(
                    searchText = searchText,
                    placeholderText = EMPLOYEE_SEARCH_PLACEHOLDER,
                    onClearClick = viewModel::clearSearchText,
                    onSearchTextChanged = viewModel::searchTextChanged
                )
            } else {
                if (employees.isNotEmpty()) {
                    IconButton(
                        onClick = viewModel::selectAllItems
                    ) {
                        Icon(
                            imageVector = Icons.Default.Checklist,
                            contentDescription = Constants.SELECT_ALL_ICON
                        )
                    }

                    IconButton(
                        onClick = viewModel::openSearchBar,
                        modifier = Modifier.testTag(NAV_SEARCH_BTN)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                        )
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmallMax),
                verticalArrangement = Arrangement.spacedBy(SpaceSmall)
            ) {
                InfoText(text = "${if (selectedItems.isEmpty()) "All" else "${selectedItems.size}"} employees will be exported.")

                StandardButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(EXPORT_EMPLOYEE_BTN),
                    enabled = true,
                    text = EXPORT_EMPLOYEE_BTN_TEXT,
                    icon = Icons.Default.Upload,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    onClick = {
                        scope.launch {
                            askForPermissions()
                            val result = ImportExport.createFile(
                                context = context,
                                fileName = EXPORT_EMPLOYEE_FILE_NAME
                            )
                            exportLauncher.launch(result)
                            viewModel.onEvent(EmployeeSettingsEvent.GetExportedItems)
                        }
                    }
                )
            }
        },
        onBackClick = { onBackClick() },
        fabPosition = FabPosition.End,
        floatingActionButton = {
            ScrollToTop(
                visible = !lazyListState.isScrollingUp(),
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                }
            )
        }
    ) {
        if (employees.isEmpty()) {
            ItemNotAvailable(
                text = if (searchText.isEmpty()) EmployeeTestTags.EMPLOYEE_NOT_AVAILABLE else EmployeeTestTags.NO_ITEMS_IN_EMPLOYEE,
                buttonText = EmployeeTestTags.CREATE_NEW_EMPLOYEE,
                onClick = {
                    navController.navigate(AddEditEmployeeScreenDestination())
                }
            )
        } else {
            TrackScrollJank(scrollableState = lazyListState, stateName = "Exported Employee::List")

            LazyColumn(
                modifier = Modifier
                    .padding(SpaceSmall),
                state = lazyListState
            ) {
                items(
                    items = employees,
                    key = { it.employeeId}
                ) { item: Employee ->
                    EmployeeData(
                        item = item,
                        doesSelected = {
                            selectedItems.contains(it)
                        },
                        onClick = viewModel::selectItem,
                        onLongClick = viewModel::selectItem
                    )
                }
            }
        }
    }
}