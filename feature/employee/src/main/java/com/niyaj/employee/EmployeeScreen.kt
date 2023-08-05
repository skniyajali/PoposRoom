package com.niyaj.employee

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.data.utils.EmployeeTestTags.CREATE_NEW_EMPLOYEE
import com.niyaj.data.utils.EmployeeTestTags.DELETE_EMPLOYEE_MESSAGE
import com.niyaj.data.utils.EmployeeTestTags.DELETE_EMPLOYEE_TITLE
import com.niyaj.data.utils.EmployeeTestTags.EMPLOYEE_NOT_AVAIlABLE
import com.niyaj.data.utils.EmployeeTestTags.EMPLOYEE_SCREEN_TITLE
import com.niyaj.data.utils.EmployeeTestTags.EMPLOYEE_SEARCH_PLACEHOLDER
import com.niyaj.data.utils.EmployeeTestTags.EMPLOYEE_TAG
import com.niyaj.data.utils.EmployeeTestTags.NO_ITEMS_IN_EMPLOYEE
import com.niyaj.designsystem.theme.LightColor9
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.employee.destinations.AddEditEmployeeScreenDestination
import com.niyaj.employee.destinations.EmployeeDetailsScreenDestination
import com.niyaj.model.Employee
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.StandardScaffold
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination(
    route = Screens.EmployeeScreen
)
@Composable
fun EmployeeScreen(
    navController: NavController,
    viewModel: EmployeeViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditEmployeeScreenDestination, String>
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    val state = viewModel.employees.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()

    val lazyListState = rememberLazyListState()

    val showFab  = viewModel.totalItems.isNotEmpty()

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    val openDialog = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when(data) {
                is UiEvent.OnError -> {
                    scope.launch {
                        snackbarState.showSnackbar(data.errorMessage)
                    }
                }
                is UiEvent.OnSuccess -> {
                    scope.launch {
                        snackbarState.showSnackbar(data.successMessage)
                    }
                }
            }
        }
    }

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        } else if (showSearchBar) {
            viewModel.closeSearchBar()
        }
    }

    resultRecipient.onNavResult {result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }

                viewModel.deselectItems()
            }
        }
    }

    StandardScaffold(
        navController = navController,
        title = if (selectedItems.isEmpty()) EMPLOYEE_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                showScrollToTop = lazyListState.isScrolled,
                fabText = CREATE_NEW_EMPLOYEE,
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navController.navigate(AddEditEmployeeScreenDestination())
                },
                onClickScroll = {
                    scope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                }
            )
        },
        navActions = {
            ScaffoldNavActions(
                placeholderText = EMPLOYEE_SEARCH_PLACEHOLDER,
                showSettingsIcon = true,
                selectionCount = selectedItems.size,
                showSearchIcon = showSearchBar,
                searchText = searchText,
                onEditClick = {
                  navController.navigate(AddEditEmployeeScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {
                    openDialog.value = true
                },
                onSettingsClick = {},
                onSelectAllClick = viewModel::selectAllItems,
                onClearClick = viewModel::clearSearchText,
                onSearchClick = viewModel::openSearchBar,
                onSearchTextChanged = viewModel::searchTextChanged
            )
        },
        fabPosition = if (lazyListState.isScrolled) FabPosition.End else FabPosition.Center,
        selectionCount = selectedItems.size,
        showBackButton = showSearchBar,
        onDeselect = viewModel::deselectItems,
        onBackClick = viewModel::closeSearchBar,
        snackbarHostState = snackbarState,
    ) { _ ->
        when (state) {
            is UiState.Loading -> LoadingIndicator()
            is UiState.Empty -> {
                ItemNotAvailable(
                    text = if (searchText.isEmpty()) EMPLOYEE_NOT_AVAIlABLE else NO_ITEMS_IN_EMPLOYEE,
                    buttonText = CREATE_NEW_EMPLOYEE,
                    onClick = {
                        navController.navigate(AddEditEmployeeScreenDestination())
                    }
                )
            }
            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(SpaceSmall),
                    state = lazyListState
                ) {
                    items(
                        items = state.data,
                        key = { it.employeeId}
                    ) { item: Employee ->
                        EmployeeData(
                            item = item,
                            doesSelected = {
                                selectedItems.contains(it)
                            },
                            onClick = {
                                if (selectedItems.isNotEmpty()) {
                                    viewModel.selectItem(it)
                                }else {
                                    navController.navigate(EmployeeDetailsScreenDestination(item.employeeId))
                                }
                            },
                            onLongClick = viewModel::selectItem
                        )
                    }
                }
            }
        }
    }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
                viewModel.deselectItems()
            },
            title = {
                Text(text = DELETE_EMPLOYEE_TITLE)
            },
            text = {
                Text(
                    text = DELETE_EMPLOYEE_MESSAGE
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        viewModel.deleteItems()
                    },
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        viewModel.deselectItems()
                    },
                ) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(28.dp)
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmployeeData(
    modifier: Modifier = Modifier,
    item: Employee,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
) {
    val borderStroke = if (doesSelected(item.employeeId)) border else null

    ListItem(
        modifier = modifier
            .testTag(EMPLOYEE_TAG.plus(item.employeeId))
            .fillMaxWidth()
            .padding(SpaceSmall)
            .then(borderStroke?.let {
                Modifier.border(it, RoundedCornerShape(SpaceMini))
            } ?: Modifier)
            .clip(RoundedCornerShape(SpaceMini))
            .combinedClickable(
                onClick = {
                    onClick(item.employeeId)
                },
                onLongClick = {
                    onLongClick(item.employeeId)
                },
            ),
        headlineContent = {
            Text(
                text = item.employeeName,
                style = MaterialTheme.typography.labelLarge
            )
        },
        supportingContent = {
            Text(text = item.employeePhone)
        },
        leadingContent = {
            CircularBox(
                icon = Icons.Default.Person,
                doesSelected = doesSelected(item.employeeId),
                text = item.employeeName
            )
        },
        trailingContent = {
            Icon(
                Icons.Filled.ArrowRight,
                contentDescription = "Localized description",
            )
        },
        shadowElevation = 1.dp,
        tonalElevation = 1.dp,
        colors = ListItemDefaults.colors(
            containerColor = LightColor9
        )
    )
}