package com.niyaj.charges

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FabPosition
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.charges.destinations.AddEditChargesScreenDestination
import com.niyaj.charges.destinations.ChargesExportScreenDestination
import com.niyaj.charges.destinations.ChargesImportScreenDestination
import com.niyaj.charges.destinations.ChargesSettingsScreenDestination
import com.niyaj.common.tags.ChargesTestTags.CHARGES_NOT_AVAILABLE
import com.niyaj.common.tags.ChargesTestTags.CHARGES_SCREEN_TITLE
import com.niyaj.common.tags.ChargesTestTags.CHARGES_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.ChargesTestTags.CHARGES_TAG
import com.niyaj.common.tags.ChargesTestTags.CREATE_NEW_CHARGES
import com.niyaj.common.tags.ChargesTestTags.DELETE_CHARGES_MESSAGE
import com.niyaj.common.tags.ChargesTestTags.DELETE_CHARGES_TITLE
import com.niyaj.common.tags.ChargesTestTags.NO_ITEMS_IN_CHARGES
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Charges
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardElevatedCard
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.StandardScaffold
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination(route = Screens.CHARGES_SCREEN)
@Composable
fun ChargesScreen(
    navController: NavController,
    viewModel: ChargesViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditChargesScreenDestination, String>,
    exportRecipient: ResultRecipient<ChargesExportScreenDestination, String>,
    importRecipient: ResultRecipient<ChargesImportScreenDestination, String>,
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    val state = viewModel.charges.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()

    val lazyGridState = rememberLazyGridState()

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

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                viewModel.deselectItems()
            }

            is NavResult.Value -> {
                scope.launch {
                    viewModel.deselectItems()
                    snackbarState.showSnackbar(result.value)
                }
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

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        } else if (showSearchBar) {
            viewModel.closeSearchBar()
        } else {
            navController.navigateUp()
        }
    }
    
    TrackScreenViewEvent(screenName = Screens.CHARGES_SCREEN)

    StandardScaffold(
        navController = navController,
        title = if (selectedItems.isEmpty()) CHARGES_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                showScrollToTop = lazyGridState.isScrolled,
                fabText = CREATE_NEW_CHARGES,
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navController.navigate(AddEditChargesScreenDestination())

                },
                onClickScroll = {
                    scope.launch {
                        lazyGridState.animateScrollToItem(0)
                    }
                }
            )
        },
        navActions = {
            ScaffoldNavActions(
                placeholderText = CHARGES_SEARCH_PLACEHOLDER,
                showSettingsIcon = true,
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = {
                    navController.navigate(AddEditChargesScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {
                    openDialog.value = true
                },
                onSettingsClick = {
                    navController.navigate(ChargesSettingsScreenDestination)
                },
                onSelectAllClick = viewModel::selectAllItems,
                onClearClick = viewModel::clearSearchText,
                onSearchClick = viewModel::openSearchBar,
                onSearchTextChanged = viewModel::searchTextChanged
            )
        },
        fabPosition = if (lazyGridState.isScrolled) FabPosition.End else FabPosition.Center,
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
                    text = if (searchText.isEmpty()) CHARGES_NOT_AVAILABLE else NO_ITEMS_IN_CHARGES,
                    buttonText = CREATE_NEW_CHARGES,
                    onClick = {
                        navController.navigate(AddEditChargesScreenDestination())
                    }
                )
            }

            is UiState.Success -> {
                TrackScrollJank(scrollableState = lazyGridState, stateName = "Charges::List")

                LazyVerticalGrid(
                    modifier = Modifier
                        .padding(SpaceSmall),
                    columns = GridCells.Fixed(2),
                    state = lazyGridState,
                ) {
                    items(
                        items = state.data,
                        key = { it.chargesId }
                    ) { item: Charges ->
                        ChargesData(
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
                Text(text = DELETE_CHARGES_TITLE)
            },
            text = {
                Text(
                    text = DELETE_CHARGES_MESSAGE
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
fun ChargesData(
    modifier: Modifier = Modifier,
    item: Charges,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
) = trace("ChargesData") {
    StandardElevatedCard(
        modifier = modifier,
        testTag = CHARGES_TAG.plus(item.chargesId),
        doesSelected = doesSelected(item.chargesId),
        onClick = {
            onClick(item.chargesId)
        },
        onLongClick = {
            onLongClick(item.chargesId)
        }
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
                Text(
                    text = item.chargesName,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(SpaceSmall))
                Text(text = item.chargesPrice.toRupee)
            }

            CircularBox(
                icon = Icons.Default.Bolt,
                doesSelected = doesSelected(item.chargesId),
                showBorder = !item.isApplicable
            )
        }
    }
}