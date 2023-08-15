package com.niyaj.employee_payment

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedAssistChip
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.common.tags.PaymentScreenTags.CREATE_NEW_PAYMENT
import com.niyaj.common.tags.PaymentScreenTags.DELETE_PAYMENT_MESSAGE
import com.niyaj.common.tags.PaymentScreenTags.DELETE_PAYMENT_TITLE
import com.niyaj.common.tags.PaymentScreenTags.NO_ITEMS_IN_PAYMENT
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_NOT_AVAIlABLE
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_SCREEN_TITLE
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_TAG
import com.niyaj.designsystem.theme.IconSizeSmall
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.employee_payment.destinations.AddEditPaymentScreenDestination
import com.niyaj.model.Payment
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.StandardScaffold
import com.niyaj.ui.components.TextWithIcon
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

@OptIn(ExperimentalFoundationApi::class)
@RootNavGraph(start = true)
@Destination(
    route = Screens.PaymentScreen
)
@Composable
fun PaymentScreen(
    navController: NavController,
    viewModel: PaymentViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditPaymentScreenDestination, String>
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    val state = viewModel.payments.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()

    val lazyListState = rememberLazyListState()
    val showScrollToTop = lazyListState.isScrolled

    var showFab by remember {
        mutableStateOf(false)
    }

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
        title = if (selectedItems.isEmpty()) PAYMENT_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                showScrollToTop = lazyListState.isScrolled,
                fabText = CREATE_NEW_PAYMENT,
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                containerColor = MaterialTheme.colorScheme.surface,
                onFabClick = {
                    navController.navigate(AddEditPaymentScreenDestination())
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
                placeholderText = PAYMENT_SEARCH_PLACEHOLDER,
                showSettingsIcon = true,
                selectionCount = selectedItems.size,
                showSearchIcon = showSearchBar,
                searchText = searchText,
                onEditClick = {
                  navController.navigate(AddEditPaymentScreenDestination(selectedItems.first()))
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
                    text = if (searchText.isEmpty()) PAYMENT_NOT_AVAIlABLE else NO_ITEMS_IN_PAYMENT,
                    buttonText = CREATE_NEW_PAYMENT,
                    onClick = {
                        navController.navigate(AddEditPaymentScreenDestination())
                    }
                )
            }
            is UiState.Success -> {
                showFab = true
                LazyColumn(
                    modifier = Modifier
                        .padding(SpaceSmall),
                    state = lazyListState
                ) {
                    state.data.forEachIndexed { _, payments ->
                        if (payments.payments.isNotEmpty()) {
                            stickyHeader {
                                TextWithIcon(
                                    modifier = Modifier
                                        .background(
                                            if (showScrollToTop) MaterialTheme.colorScheme.surface else Color.Transparent
                                        )
                                        .clip(
                                            RoundedCornerShape(if (showScrollToTop) 4.dp else 0.dp)
                                        ),
                                    isTitle = true,
                                    text = payments.employee.employeeName,
                                    icon = Icons.Default.Person
                                )
                            }

                            items(
                                items = payments.payments,
                                key = { it.paymentId }
                            ) { item ->
                                PaymentData(
                                    employeeName = payments.employee.employeeName,
                                    item = item,
                                    doesSelected = {
                                        selectedItems.contains(it)
                                    },
                                    onClick = {
                                        if (selectedItems.isNotEmpty()) {
                                            viewModel.selectItem(it)
                                        }
                                    },
                                    onLongClick = viewModel::selectItem
                                )
                            }
                        }
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
                Text(text = DELETE_PAYMENT_TITLE)
            },
            text = {
                Text(
                    text = DELETE_PAYMENT_MESSAGE
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
fun PaymentData(
    modifier: Modifier = Modifier,
    employeeName: String,
    item: Payment,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
) {
    val borderStroke = if (doesSelected(item.paymentId)) border else null

    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        modifier = modifier
            .testTag(PAYMENT_TAG.plus(item.paymentId))
            .fillMaxWidth()
            .padding(SpaceSmall)
            .then(borderStroke?.let {
                Modifier.border(it, RoundedCornerShape(SpaceMini))
            } ?: Modifier)
            .clip(RoundedCornerShape(SpaceMini))
            .combinedClickable(
                onClick = {
                    onClick(item.paymentId)
                },
                onLongClick = {
                    onLongClick(item.paymentId)
                },
            ),
        leadingContent = {
            CircularBox(
                icon = Icons.Default.Money,
                doesSelected = doesSelected(item.paymentId),
                text = employeeName
            )
        },
        headlineContent = {
            Text(
                text = item.paymentAmount.toRupee,
                style = MaterialTheme.typography.labelLarge
            )
        },
        overlineContent = {
            Text(text = item.paymentDate.toPrettyDate())
        },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AssistChip(
                    onClick = { },
                    label = {
                        Text(
                            text = item.paymentType.name,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.MergeType,
                            contentDescription = null,
                            modifier = Modifier.size(IconSizeSmall)
                        )
                    }
                )
                Spacer(modifier = Modifier.width(SpaceSmall))
                ElevatedAssistChip(
                    onClick = { },
                    label = {
                        Text(
                            text = item.paymentMode.name,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            modifier = Modifier.size(IconSizeSmall)
                        )
                    },
                    colors = AssistChipDefaults.elevatedAssistChipColors()
                )
            }
        }
    )
}