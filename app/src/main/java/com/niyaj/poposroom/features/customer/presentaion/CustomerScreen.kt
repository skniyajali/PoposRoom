package com.niyaj.poposroom.features.customer.presentaion

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
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.poposroom.features.common.components.CircularBox
import com.niyaj.poposroom.features.common.components.ItemNotAvailable
import com.niyaj.poposroom.features.common.components.LoadingIndicator
import com.niyaj.poposroom.features.common.components.StandardScaffold
import com.niyaj.poposroom.features.common.event.UiState
import com.niyaj.poposroom.features.common.ui.theme.SpaceMini
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall
import com.niyaj.poposroom.features.common.utils.SheetScreen
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.common.utils.isScrolled
import com.niyaj.poposroom.features.customer.domain.model.Customer
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.CREATE_NEW_CUSTOMER
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.CUSTOMER_NOT_AVAIlABLE
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.CUSTOMER_SCREEN_TITLE
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.CUSTOMER_SEARCH_PLACEHOLDER
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.CUSTOMER_TAG
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.DELETE_CUSTOMER_MESSAGE
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.DELETE_CUSTOMER_TITLE
import com.niyaj.poposroom.features.customer.domain.utils.CustomerTestTags.NO_ITEMS_IN_CUSTOMER
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun CustomerScreen(
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    navController: NavController,
    onCloseSheet: () -> Unit = {},
    onOpenSheet: (SheetScreen) -> Unit = {},
    viewModel: CustomerViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    val state = viewModel.charges.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedAddOnItems.toList()

    val lazyListState = rememberLazyListState()

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
                is UiEvent.IsLoading -> {}
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
        if (bottomSheetScaffoldState.bottomSheetState.hasExpandedState) {
            onCloseSheet()
        }
    }

    StandardScaffold(
        navController = navController,
        snackbarHostState = snackbarState,
        title = if (selectedItems.isEmpty()) CUSTOMER_SCREEN_TITLE else "${selectedItems.size} Selected",
        showFab = showFab,
        fabText = CREATE_NEW_CUSTOMER,
        placeholderText = CUSTOMER_SEARCH_PLACEHOLDER,
        fabExtended = !lazyListState.isScrolled,
        showSearchBar = showSearchBar,
        selectionCount = selectedItems.size,
        searchText = searchText,
        showBackButton = showSearchBar,
        onFabClick = {
            onOpenSheet(SheetScreen.CreateNewCustomer)
        },
        onEditClick = {
            onOpenSheet(SheetScreen.UpdateCustomer(selectedItems.first()))
        },
        onDeleteClick = {
            openDialog.value = true
        },
        onDeselect = viewModel::deselectItems,
        onSelectAllClick = viewModel::selectAllItems,
        onSearchTextChanged = viewModel::searchTextChanged,
        onSearchClick = viewModel::openSearchBar,
        onBackClick = viewModel::closeSearchBar,
        onClearClick = viewModel::clearSearchText
    ) { _ ->
        when (state) {
            is UiState.Loading -> LoadingIndicator()
            is UiState.Empty -> {
                ItemNotAvailable(
                    text = if (searchText.isEmpty()) CUSTOMER_NOT_AVAIlABLE else NO_ITEMS_IN_CUSTOMER,
                    buttonText = CREATE_NEW_CUSTOMER,
                    onClick = {
                        onOpenSheet(SheetScreen.CreateNewCustomer)
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
                    items(
                        items = state.data,
                        key = { it.customerId}
                    ) { item: Customer ->
                        CustomerData(
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

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
                viewModel.deselectItems()
            },
            title = {
                Text(text = DELETE_CUSTOMER_TITLE)
            },
            text = {
                Text(
                    text = DELETE_CUSTOMER_MESSAGE
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
fun CustomerData(
    modifier: Modifier = Modifier,
    item: Customer,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
) {
    val borderStroke = if (doesSelected(item.customerId)) border else null

    ListItem(
        modifier = modifier
            .testTag(CUSTOMER_TAG.plus(item.customerId))
            .fillMaxWidth()
            .padding(SpaceSmall)
            .then(borderStroke?.let {
                Modifier.border(it, RoundedCornerShape(SpaceMini))
            } ?: Modifier)
            .clip(RoundedCornerShape(SpaceMini))
            .combinedClickable(
                onClick = {
                    onClick(item.customerId)
                },
                onLongClick = {
                    onLongClick(item.customerId)
                },
            ),
        headlineContent = {
            Text(
                text = item.customerPhone,
                style = MaterialTheme.typography.labelLarge
            )
        },
        supportingContent = item.customerName?.let {
            {
              Text(
                  text = it
              )
            }
        },
        leadingContent = {
            CircularBox(
                icon = Icons.Default.Person,
                doesSelected = doesSelected(item.customerId),
                text = item.customerName
            )
        },
        trailingContent = {
            Icon(
                Icons.Filled.ArrowRight,
                contentDescription = "Localized description",
            )
        }
    )
}