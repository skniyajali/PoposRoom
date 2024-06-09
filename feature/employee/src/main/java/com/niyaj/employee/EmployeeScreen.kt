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

package com.niyaj.employee

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.EmployeeTestTags.CREATE_NEW_EMPLOYEE
import com.niyaj.common.tags.EmployeeTestTags.DELETE_EMPLOYEE_MESSAGE
import com.niyaj.common.tags.EmployeeTestTags.DELETE_EMPLOYEE_TITLE
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_NOT_AVAILABLE
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_SCREEN_TITLE
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_TAG
import com.niyaj.common.tags.EmployeeTestTags.NO_ITEMS_IN_EMPLOYEE
import com.niyaj.designsystem.components.StandardAssistChip
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.employee.destinations.AddEditEmployeeScreenDestination
import com.niyaj.employee.destinations.EmployeeDetailsScreenDestination
import com.niyaj.employee.destinations.EmployeeExportScreenDestination
import com.niyaj.employee.destinations.EmployeeImportScreenDestination
import com.niyaj.employee.destinations.EmployeeSettingsScreenDestination
import com.niyaj.model.Employee
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.PoposPrimaryScaffold
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination(route = Screens.EMPLOYEE_SCREEN)
@Composable
fun EmployeeScreen(
    navigator: DestinationsNavigator,
    viewModel: EmployeeViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditEmployeeScreenDestination, String>,
    exportRecipient: ResultRecipient<EmployeeExportScreenDestination, String>,
    importRecipient: ResultRecipient<EmployeeImportScreenDestination, String>,
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    val state = viewModel.employees.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()

    val lazyListState = rememberLazyListState()

    val showFab = viewModel.totalItems.isNotEmpty()

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    val openDialog = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
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
        } else {
            navigator.popBackStack()
        }
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                viewModel.deselectItems()
            }
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }

                viewModel.deselectItems()
            }
        }
    }

    exportRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }

    importRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }

    TrackScreenViewEvent(screenName = Screens.EMPLOYEE_SCREEN)

    PoposPrimaryScaffold(
        currentRoute = Screens.EMPLOYEE_SCREEN,
        title = if (selectedItems.isEmpty()) EMPLOYEE_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navigator.navigate(AddEditEmployeeScreenDestination())
                },
                onClickScroll = {
                    scope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                },
                showScrollToTop = lazyListState.isScrolled,
                fabText = CREATE_NEW_EMPLOYEE,
            )
        },
        navActions = {
            ScaffoldNavActions(
                placeholderText = EMPLOYEE_SEARCH_PLACEHOLDER,
                showSettingsIcon = true,
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = {
                    navigator.navigate(AddEditEmployeeScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {
                    openDialog.value = true
                },
                onSettingsClick = {
                    navigator.navigate(EmployeeSettingsScreenDestination)
                },
                onSelectAllClick = viewModel::selectAllItems,
                onClearClick = viewModel::clearSearchText,
                onSearchIconClick = viewModel::openSearchBar,
                onSearchTextChanged = viewModel::searchTextChanged,
            )
        },
        fabPosition = if (lazyListState.isScrolled) FabPosition.End else FabPosition.Center,
        selectionCount = selectedItems.size,
        showBackButton = showSearchBar,
        onDeselect = viewModel::deselectItems,
        onBackClick = viewModel::closeSearchBar,
        snackbarHostState = snackbarState,
        onNavigateToScreen = navigator::navigate,
    ) { _ ->
        when (state) {
            is UiState.Loading -> LoadingIndicator()

            is UiState.Empty -> {
                ItemNotAvailable(
                    text = if (searchText.isEmpty()) EMPLOYEE_NOT_AVAILABLE else NO_ITEMS_IN_EMPLOYEE,
                    buttonText = CREATE_NEW_EMPLOYEE,
                    onClick = {
                        navigator.navigate(AddEditEmployeeScreenDestination())
                    },
                )
            }

            is UiState.Success -> {
                TrackScrollJank(scrollableState = lazyListState, stateName = "Employee::List")

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(SpaceSmall),
                    state = lazyListState,
                ) {
                    items(
                        items = state.data,
                        key = { it.employeeId },
                    ) { item: Employee ->
                        EmployeeData(
                            item = item,
                            doesSelected = {
                                selectedItems.contains(it)
                            },
                            onClick = {
                                if (selectedItems.isNotEmpty()) {
                                    viewModel.selectItem(it)
                                } else {
                                    navigator.navigate(EmployeeDetailsScreenDestination(item.employeeId))
                                }
                            },
                            onLongClick = viewModel::selectItem,
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
            title = DELETE_EMPLOYEE_TITLE,
            message = DELETE_EMPLOYEE_MESSAGE,
            onConfirm = {
                openDialog.value = false
                viewModel.deleteItems()
            },
            onDismiss = {
                openDialog.value = false
                viewModel.deselectItems()
            },
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
) = trace("EmployeeData") {
    val borderStroke = if (doesSelected(item.employeeId)) border else null

    ListItem(
        modifier = modifier
            .testTag(EMPLOYEE_TAG.plus(item.employeeId))
            .fillMaxWidth()
            .padding(SpaceSmall)
            .then(
                borderStroke?.let {
                    Modifier.border(it, RoundedCornerShape(SpaceMini))
                } ?: Modifier,
            )
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
                style = MaterialTheme.typography.labelLarge,
            )
        },
        supportingContent = {
            Text(text = item.employeePhone)
        },
        leadingContent = {
            CircularBox(
                icon = PoposIcons.Person,
                doesSelected = doesSelected(item.employeeId),
                text = item.employeeName,
            )
        },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SpaceMini),
            ) {
                if (item.isDeliveryPartner) {
                    StandardAssistChip(
                        text = "Delivery Partner",
                        icon = PoposIcons.DeliveryDining,
                    )
                }

                Icon(
                    PoposIcons.ArrowRightAlt,
                    contentDescription = "Localized description",
                )
            }
        },
        shadowElevation = 1.dp,
        tonalElevation = 1.dp,
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    )
}
