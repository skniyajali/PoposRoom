package com.niyaj.poposroom.features.customer.presentaion

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.poposroom.features.common.components.ItemNotAvailable
import com.niyaj.poposroom.features.common.components.LoadingIndicator
import com.niyaj.poposroom.features.common.components.StandardScaffold
import com.niyaj.poposroom.features.common.event.UiState
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

    val lazyGridState = rememberLazyGridState()

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
        fabExtended = !lazyGridState.isScrolled,
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

                LazyVerticalGrid(
                    modifier = Modifier
                        .padding(SpaceSmall),
                    columns = GridCells.Fixed(2),
                    state = lazyGridState,
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

    ElevatedCard(
        modifier = modifier
            .testTag(CUSTOMER_TAG.plus(item.customerId))
            .padding(SpaceSmall)
            .then(borderStroke?.let {
                Modifier.border(it, CardDefaults.elevatedShape)
            } ?: Modifier)
            .clip(CardDefaults.elevatedShape)
            .combinedClickable(
                onClick = {
                    onClick(item.customerId)
                },
                onLongClick = {
                    onLongClick(item.customerId)
                },
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = item.customerPhone)
                Spacer(modifier = Modifier.height(SpaceSmall))
//                Text(text = item.chargesPrice.toRupee)
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (doesSelected(item.customerId)) Icons.Default.Check
                    else Icons.Default.Bolt,
                    contentDescription = item.customerPhone,
                    tint = if (doesSelected(item.customerId)) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceTint,
                )
            }
        }
    }
}