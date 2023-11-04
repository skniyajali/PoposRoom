package com.niyaj.employee_payment

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MergeType
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.PaymentScreenTags.CREATE_NEW_PAYMENT
import com.niyaj.common.tags.PaymentScreenTags.DELETE_PAYMENT_MESSAGE
import com.niyaj.common.tags.PaymentScreenTags.DELETE_PAYMENT_TITLE
import com.niyaj.common.tags.PaymentScreenTags.NO_ITEMS_IN_PAYMENT
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_NOT_AVAIlABLE
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_SCREEN_TITLE
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_TAG
import com.niyaj.common.utils.toBarDate
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.IconSizeSmall
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.employee_payment.destinations.AddEditPaymentScreenDestination
import com.niyaj.employee_payment.destinations.PaymentSettingsScreenDestination
import com.niyaj.model.Employee
import com.niyaj.model.Payment
import com.niyaj.model.PaymentMode
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardAssistChip
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.StandardFilterChip
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
    route = Screens.PAYMENT_SCREEN
)
@Composable
fun PaymentScreen(
    navController: NavController,
    viewModel: PaymentViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditPaymentScreenDestination, String>,
    onClickEmployee: (employeeId: Int) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    val state = viewModel.payments.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()

    val lazyListState = rememberLazyListState()

    val showFab = viewModel.totalItems.toList()

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
        }
    }

    resultRecipient.onNavResult { result ->
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

    var listView by remember {
        mutableStateOf(false)
    }

    StandardScaffold(
        navController = navController,
        title = if (selectedItems.isEmpty()) PAYMENT_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                showScrollToTop = lazyListState.isScrolled,
                fabText = CREATE_NEW_PAYMENT,
                fabVisible = (showFab.isNotEmpty() && selectedItems.isEmpty() && !showSearchBar),
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
                showSearchIcon = true,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = {
                    navController.navigate(AddEditPaymentScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {
                    openDialog.value = true
                },
                onSettingsClick = {
                    navController.navigate(PaymentSettingsScreenDestination)
                },
                onSelectAllClick = viewModel::selectAllItems,
                onClearClick = viewModel::clearSearchText,
                onSearchClick = viewModel::openSearchBar,
                onSearchTextChanged = viewModel::searchTextChanged,
                content = {
                    IconButton(
                        onClick = {
                            listView = !listView
                        }
                    ) {
                        Icon(
                            imageVector = if (listView) Icons.Default.ViewAgenda
                            else Icons.Default.CalendarViewDay,
                            contentDescription = "Change View"
                        )
                    }
                }
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
                LazyColumn(
                    modifier = Modifier
                        .padding(SpaceSmall),
                    state = lazyListState
                ) {
                    itemsIndexed(
                        items = state.data,
                        key = { _, item ->
                            item.employee.employeeId
                        }
                    ) { _, empWithPayments ->
                        PaymentData(
                            showListView = listView,
                            employee = empWithPayments.employee,
                            payments = empWithPayments.payments,
                            doesSelected = {
                                selectedItems.contains(it)
                            },
                            onClick = {
                                if (selectedItems.isNotEmpty()) {
                                    viewModel.selectItem(it)
                                }
                            },
                            onLongClick = viewModel::selectItem,
                            onClickAddPayment = {
                                navController.navigate(AddEditPaymentScreenDestination(employeeId = it))
                            },
                            onClickEmployee = onClickEmployee
                        )

                        Spacer(modifier = Modifier.height(SpaceMedium))
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


@Composable
fun PaymentData(
    modifier: Modifier = Modifier,
    showListView: Boolean,
    employee: Employee,
    payments: List<Payment>,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    onClickAddPayment: (employeeId: Int) -> Unit,
    onClickEmployee: (employeeId: Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        ListItem(
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            modifier = modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                ) {
                    onClickEmployee(employee.employeeId)
                },
            leadingContent = {
                CircularBox(
                    icon = Icons.Default.Money,
                    doesSelected = false,
                    text = employee.employeeName
                )
            },
            headlineContent = {
                Text(
                    text = employee.employeeName,
                    style = MaterialTheme.typography.labelLarge
                )
            },
            supportingContent = {
                Text(text = employee.employeePhone)
            },
            trailingContent = {
                FilledTonalIconButton(
                    onClick = {
                        onClickAddPayment(employee.employeeId)
                    },
                    shape = RoundedCornerShape(SpaceMini),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add New Payment",
                    )
                }
            }
        )

        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceSmall))

        payments.forEachIndexed { index, salary ->
            if (showListView) {
                EmployeePayment(
                    modifier = Modifier,
                    payment = salary,
                    doesSelected = doesSelected,
                    onClick = onClick,
                    onLongClick = onLongClick,
                    border = border
                )

                if (index != payments.size - 1) {
                    Spacer(modifier = Modifier.height(SpaceSmall))
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = SpaceSmall)
                    )
                    Spacer(modifier = Modifier.height(SpaceSmall))
                }

            } else {
                EmployeePaymentCardView(
                    modifier = Modifier,
                    payment = salary,
                    doesSelected = doesSelected,
                    onClick = onClick,
                    onLongClick = onLongClick,
                )

                if (index != payments.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = SpaceSmall)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmployeePayment(
    modifier: Modifier = Modifier,
    payment: Payment,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
) {
    val borderStroke = if (doesSelected(payment.paymentId)) border else null

    Box(
        modifier = modifier
            .fillMaxWidth()
            .testTag(PAYMENT_TAG.plus(payment.paymentId))
            .then(borderStroke?.let {
                Modifier.border(it)
            } ?: Modifier)
            .combinedClickable(
                onClick = {
                    onClick(payment.paymentId)
                },
                onLongClick = {
                    onLongClick(payment.paymentId)
                },
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = payment.paymentAmount.toRupee,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(0.8F)
            )

            Text(
                text = payment.paymentDate.toBarDate,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(0.8F),
            )

            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                StandardFilterChip(
                    text = payment.paymentMode.name,
                    icon = when (payment.paymentMode) {
                        PaymentMode.Cash -> Icons.Default.Money
                        PaymentMode.Online -> Icons.Default.AccountBalance
                        else -> Icons.Default.Payments
                    },
                    selected = false,
                )

                Spacer(modifier = Modifier.width(SpaceSmall))

                StandardAssistChip(
                    text = payment.paymentType.name,
                    icon = Icons.AutoMirrored.Filled.MergeType
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmployeePaymentCardView(
    modifier: Modifier = Modifier,
    payment: Payment,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
) {
    ListItem(
        colors = ListItemDefaults.colors(),
        modifier = modifier
            .fillMaxWidth()
            .testTag(PAYMENT_TAG.plus(payment.paymentId))
            .combinedClickable(
                onClick = {
                    onClick(payment.paymentId)
                },
                onLongClick = {
                    onLongClick(payment.paymentId)
                },
            ),
        leadingContent = {
            CircularBox(
                icon = when (payment.paymentMode) {
                    PaymentMode.Cash -> Icons.Default.Money
                    PaymentMode.Online -> Icons.Default.AccountBalance
                    else -> Icons.Default.Payments
                },
                doesSelected = doesSelected(payment.paymentId),
            )
        },
        headlineContent = {
            Text(
                text = payment.paymentAmount.toRupee,
                style = MaterialTheme.typography.labelLarge
            )
        },
        supportingContent = {
            Text(text = payment.paymentDate.toBarDate)
        },
        trailingContent = {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                StandardFilterChip(
                    text = payment.paymentMode.name,
                    icon = when (payment.paymentMode) {
                        PaymentMode.Cash -> Icons.Default.Money
                        PaymentMode.Online -> Icons.Default.AccountBalance
                        else -> Icons.Default.Payments
                    },
                )

                Spacer(modifier = Modifier.width(SpaceSmall))

                StandardAssistChip(
                    text = payment.paymentType.name,
                    icon = Icons.AutoMirrored.Filled.MergeType
                )
            }
        }
    )
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
                            imageVector = Icons.AutoMirrored.Filled.MergeType,
                            contentDescription = "Payment Type",
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
                            contentDescription = "Payments Mode",
                            modifier = Modifier.size(IconSizeSmall)
                        )
                    },
                    colors = AssistChipDefaults.elevatedAssistChipColors()
                )
            }
        }
    )
}