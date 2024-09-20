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

package com.niyaj.employeePayment

import androidx.activity.compose.BackHandler
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.PaymentScreenTags.CREATE_NEW_PAYMENT
import com.niyaj.common.tags.PaymentScreenTags.DELETE_PAYMENT_MESSAGE
import com.niyaj.common.tags.PaymentScreenTags.DELETE_PAYMENT_TITLE
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_NOT_AVAILABLE
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_SCREEN_TITLE
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_SEARCH_PLACEHOLDER
import com.niyaj.common.utils.Constants.SEARCH_ITEM_NOT_FOUND
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.employeePayment.components.EmployeePaymentList
import com.niyaj.employeePayment.components.ViewType
import com.niyaj.employeePayment.destinations.AddEditPaymentScreenDestination
import com.niyaj.employeePayment.destinations.PaymentExportScreenDestination
import com.niyaj.employeePayment.destinations.PaymentImportScreenDestination
import com.niyaj.employeePayment.destinations.PaymentSettingsScreenDestination
import com.niyaj.model.EmployeeWithPayments
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.PoposPrimaryScaffold
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.PaymentPreviewParameter
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
import com.niyaj.ui.utils.navigate
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination(route = Screens.PAYMENT_SCREEN)
@Composable
fun PaymentScreen(
    navigator: DestinationsNavigator,
    onClickEmployee: (employeeId: Int) -> Unit,
    resultRecipient: ResultRecipient<AddEditPaymentScreenDestination, String>,
    exportRecipient: ResultRecipient<PaymentExportScreenDestination, String>,
    importRecipient: ResultRecipient<PaymentImportScreenDestination, String>,
    modifier: Modifier = Modifier,
    viewModel: PaymentViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }

    val state by viewModel.payments.collectAsStateWithLifecycle()
    val showSearchBar by viewModel.showSearchBar.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    val selectedItems = viewModel.selectedItems.toList()
    val searchText = viewModel.searchText.value

    PaymentScreenContent(
        uiState = state,
        selectedItems = selectedItems,
        showSearchBar = showSearchBar,
        searchText = searchText,
        onClickSearchIcon = viewModel::openSearchBar,
        onSearchTextChanged = viewModel::searchTextChanged,
        onClickClear = viewModel::clearSearchText,
        onCloseSearchBar = viewModel::closeSearchBar,
        onClickSelectItem = viewModel::selectItem,
        onClickSelectAll = viewModel::selectAllItems,
        onClickDeselect = viewModel::deselectItems,
        onClickDelete = viewModel::deleteItems,
        onClickBack = navigator::popBackStack,
        onNavigateToScreen = navigator::navigate,
        onClickCreateNew = {
            navigator.navigate(AddEditPaymentScreenDestination())
        },
        onClickEdit = {
            navigator.navigate(AddEditPaymentScreenDestination(it))
        },
        onClickSettings = {
            navigator.navigate(PaymentSettingsScreenDestination)
        },
        onClickAddPayment = {
            navigator.navigate(AddEditPaymentScreenDestination(employeeId = it))
        },
        onClickEmployee = onClickEmployee,
        modifier = modifier,
        snackbarState = snackbarState,
    )

    HandleResultRecipients(
        resultRecipient = resultRecipient,
        exportRecipient = exportRecipient,
        importRecipient = importRecipient,
        event = event,
        onDeselectItems = viewModel::deselectItems,
        coroutineScope = scope,
        snackbarHostState = snackbarState,
    )
}

@VisibleForTesting
@Composable
internal fun PaymentScreenContent(
    uiState: UiState<List<EmployeeWithPayments>>,
    selectedItems: List<Int>,
    showSearchBar: Boolean,
    searchText: String,
    onClickSearchIcon: () -> Unit,
    onSearchTextChanged: (String) -> Unit,
    onClickClear: () -> Unit,
    onCloseSearchBar: () -> Unit,
    onClickSelectItem: (Int) -> Unit,
    onClickSelectAll: () -> Unit,
    onClickDeselect: () -> Unit,
    onClickDelete: () -> Unit,
    onClickBack: () -> Unit,
    onNavigateToScreen: (String) -> Unit,
    onClickCreateNew: () -> Unit,
    onClickEdit: (Int) -> Unit,
    onClickSettings: () -> Unit,
    onClickAddPayment: (employeeId: Int) -> Unit,
    onClickEmployee: (employeeId: Int) -> Unit,
    modifier: Modifier = Modifier,
    snackbarState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
) {
    TrackScreenViewEvent(screenName = Screens.PAYMENT_SCREEN)

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            onClickDeselect()
        } else if (showSearchBar) {
            onCloseSearchBar()
        } else {
            onClickBack()
        }
    }

    val showFab = uiState is UiState.Success
    val openDialog = remember { mutableStateOf(false) }
    var viewType by remember { mutableStateOf(ViewType.CARD) }

    PoposPrimaryScaffold(
        currentRoute = Screens.PAYMENT_SCREEN,
        title = if (selectedItems.isEmpty()) PAYMENT_SCREEN_TITLE else "${selectedItems.size} Selected",
        selectionCount = selectedItems.size,
        floatingActionButton = {
            StandardFAB(
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = onClickCreateNew,
                onClickScroll = {
                    scope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                },
                showScrollToTop = lazyListState.isScrolled,
                fabText = CREATE_NEW_PAYMENT,
            )
        },
        navActions = {
            ScaffoldNavActions(
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                searchText = searchText,
                onEditClick = {
                    onClickEdit(selectedItems.first())
                },
                onDeleteClick = {
                    openDialog.value = true
                },
                onSelectAllClick = onClickSelectAll,
                onClearClick = onClickClear,
                onSearchIconClick = onClickSearchIcon,
                onSearchTextChanged = onSearchTextChanged,
                showSearchBar = showSearchBar,
                showSettingsIcon = true,
                onSettingsClick = onClickSettings,
                content = {
                    if (showFab) {
                        IconButton(
                            onClick = {
                                viewType = when (viewType) {
                                    ViewType.LIST -> ViewType.CARD
                                    ViewType.CARD -> ViewType.LIST
                                }
                            },
                        ) {
                            Icon(
                                imageVector = if (viewType == ViewType.LIST) {
                                    PoposIcons.ViewAgenda
                                } else {
                                    PoposIcons.CalendarViewDay
                                },
                                contentDescription = "Change View",
                            )
                        }
                    }
                },
                placeholderText = PAYMENT_SEARCH_PLACEHOLDER,
            )
        },
        onBackClick = if (showSearchBar) onCloseSearchBar else onClickBack,
        onNavigateToScreen = onNavigateToScreen,
        modifier = modifier,
        fabPosition = if (lazyListState.isScrolled) FabPosition.End else FabPosition.Center,
        showBackButton = showSearchBar,
        onDeselect = onClickDeselect,
        snackbarHostState = snackbarState,
    ) {
        Crossfade(
            targetState = uiState,
            label = "PaymentList::UiState",
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) PAYMENT_NOT_AVAILABLE else SEARCH_ITEM_NOT_FOUND,
                        buttonText = CREATE_NEW_PAYMENT,
                        onClick = onClickCreateNew,
                    )
                }

                is UiState.Success -> {
                    EmployeePaymentList(
                        viewType = viewType,
                        items = state.data.toImmutableList(),
                        doesSelected = selectedItems::contains,
                        onSelectItem = onClickSelectItem,
                        modifier = Modifier,
                        isInSelectionMode = selectedItems.isNotEmpty(),
                        showTrailingIcon = true,
                        showEmployeeDetails = true,
                        onClickAddPayment = onClickAddPayment,
                        onClickEmployee = onClickEmployee,
                        lazyListState = lazyListState,
                    )
                }
            }
        }
    }

    AnimatedVisibility(
        visible = openDialog.value,
    ) {
        StandardDialog(
            title = DELETE_PAYMENT_TITLE,
            message = DELETE_PAYMENT_MESSAGE,
            onConfirm = {
                openDialog.value = false
                onClickDelete()
            },
            onDismiss = {
                openDialog.value = false
                onClickDeselect()
            },
        )
    }
}

@Composable
private fun HandleResultRecipients(
    resultRecipient: ResultRecipient<AddEditPaymentScreenDestination, String>,
    exportRecipient: ResultRecipient<PaymentExportScreenDestination, String>,
    importRecipient: ResultRecipient<PaymentImportScreenDestination, String>,
    event: UiEvent?,
    onDeselectItems: () -> Unit,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                onDeselectItems()
            }

            is NavResult.Value -> {
                onDeselectItems()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    exportRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    importRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
                is UiEvent.OnError -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(data.errorMessage)
                    }
                }

                is UiEvent.OnSuccess -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(data.successMessage)
                    }
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun PaymentScreenPreview(
    @PreviewParameter(PaymentPreviewParameter::class)
    uiState: UiState<List<EmployeeWithPayments>>,
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        PaymentScreenContent(
            uiState = uiState,
            selectedItems = listOf(),
            showSearchBar = false,
            searchText = "",
            onClickSearchIcon = {},
            onSearchTextChanged = {},
            onClickClear = {},
            onCloseSearchBar = {},
            onClickSelectItem = {},
            onClickSelectAll = {},
            onClickDeselect = {},
            onClickDelete = {},
            onClickBack = {},
            onNavigateToScreen = {},
            onClickCreateNew = {},
            onClickEdit = {},
            onClickSettings = {},
            onClickAddPayment = {},
            onClickEmployee = {},
            modifier = modifier,
        )
    }
}
