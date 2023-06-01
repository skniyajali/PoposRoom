package com.niyaj.poposroom.features.address.presentation

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
import com.niyaj.poposroom.features.address.domain.model.Address
import com.niyaj.poposroom.features.address.domain.utils.AddressTestTags.ADDRESS_ITEM_TAG
import com.niyaj.poposroom.features.address.domain.utils.AddressTestTags.ADDRESS_NOT_AVAIlABLE
import com.niyaj.poposroom.features.address.domain.utils.AddressTestTags.ADDRESS_SCREEN_TITLE
import com.niyaj.poposroom.features.address.domain.utils.AddressTestTags.CREATE_NEW_ADDRESS
import com.niyaj.poposroom.features.address.domain.utils.AddressTestTags.DELETE_ADDRESS_ITEM_MESSAGE
import com.niyaj.poposroom.features.address.domain.utils.AddressTestTags.DELETE_ADDRESS_ITEM_TITLE
import com.niyaj.poposroom.features.common.components.ItemNotAvailable
import com.niyaj.poposroom.features.common.components.LoadingIndicator
import com.niyaj.poposroom.features.common.components.StandardScaffold
import com.niyaj.poposroom.features.common.event.UiState
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall
import com.niyaj.poposroom.features.common.utils.Constants.SEARCH_ITEM_NOT_FOUND
import com.niyaj.poposroom.features.common.utils.SheetScreen
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.common.utils.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun AddressScreen(
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    navController: NavController,
    onCloseSheet: () -> Unit = {},
    onOpenSheet: (SheetScreen) -> Unit = {},
    viewModel: AddressViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    val state = viewModel.addOnItems.collectAsStateWithLifecycle().value

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
//            viewModel.onEvent(ItemEvents.DeselectAllItems)
            viewModel.deselectItems()
        } else if (showSearchBar) {
//            viewModel.onEvent(ItemEvents.OnSearchBarCloseClick)
            viewModel.closeSearchBar()
        }
        if (bottomSheetScaffoldState.bottomSheetState.hasExpandedState) {
            onCloseSheet()
        }
    }

    StandardScaffold(
        navController = navController,
        snackbarHostState = snackbarState,
        title = if (selectedItems.isEmpty()) ADDRESS_SCREEN_TITLE else "${selectedItems.size} Selected",
        showFab = showFab,
        fabText = CREATE_NEW_ADDRESS,
        fabExtended = !lazyGridState.isScrolled,
        showSearchBar = showSearchBar,
        selectionCount = selectedItems.size,
        searchText = searchText,
        showBackButton = showSearchBar,
        onFabClick = {
            onOpenSheet(SheetScreen.CreateNewAddress)
        },
        onEditClick = {
            onOpenSheet(SheetScreen.UpdateAddress(selectedItems.first()))
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
                    text = if (searchText.isEmpty()) ADDRESS_NOT_AVAIlABLE else SEARCH_ITEM_NOT_FOUND,
                    buttonText = CREATE_NEW_ADDRESS,
                    onClick = {
                        onOpenSheet(SheetScreen.CreateNewAddress)
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
                        key = {
                            it.addressName.plus(it.addressId)
                        }
                    ) { address ->
                        AddressData(
                            modifier = Modifier.testTag(ADDRESS_ITEM_TAG.plus(address.addressId)),
                            item = address,
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
                Text(text = DELETE_ADDRESS_ITEM_TITLE)
            },
            text = {
                Text(
                    text = DELETE_ADDRESS_ITEM_MESSAGE)
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
fun AddressData(
    modifier: Modifier = Modifier,
    item: Address,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
) {
    val borderStroke = if (doesSelected(item.addressId)) border else null

    ElevatedCard(
        modifier = modifier
            .padding(SpaceSmall)
            .then(borderStroke?.let {
                Modifier.border(it, CardDefaults.elevatedShape)
            } ?: Modifier)
            .clip(CardDefaults.elevatedShape)
            .combinedClickable(
                onClick = {
                    onClick(item.addressId)
                },
                onLongClick = {
                    onLongClick(item.addressId)
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
                Text(text = item.addressName)
                Spacer(modifier = Modifier.height(SpaceSmall))
                Text(text = item.shortName)
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center,
            ) {
                if (doesSelected(item.addressId)) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = item.addressName,
                        tint = if (doesSelected(item.addressId)) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceTint,
                    )
                }else {
                    Text(
                        text = item.shortName,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}