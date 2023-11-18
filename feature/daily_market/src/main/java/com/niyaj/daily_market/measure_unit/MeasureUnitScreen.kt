package com.niyaj.daily_market.measure_unit

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.MeasureUnitTestTags.CREATE_NEW_UNIT
import com.niyaj.common.tags.MeasureUnitTestTags.DELETE_ITEM_MESSAGE
import com.niyaj.common.tags.MeasureUnitTestTags.DELETE_ITEM_TITLE
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_LIST_ITEM_TAG
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_NOT_AVAILABLE
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_SCREEN_TITLE
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_SEARCH_PLACEHOLDER
import com.niyaj.common.utils.Constants
import com.niyaj.common.utils.safeString
import com.niyaj.daily_market.destinations.AddEditMeasureUnitScreenDestination
import com.niyaj.daily_market.destinations.ExportMeasureUnitScreenDestination
import com.niyaj.daily_market.destinations.ImportMeasureUnitScreenDestination
import com.niyaj.daily_market.destinations.MeasureUnitSettingsScreenDestination
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.MeasureUnit
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.StandardScaffold
import com.niyaj.ui.components.drawAnimatedBorder
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@Destination
@Composable
fun MeasureUnitScreen(
    navController: NavController,
    viewModel: MeasureUnitViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditMeasureUnitScreenDestination, String>,
    exportRecipient: ResultRecipient<ExportMeasureUnitScreenDestination, String>,
    importRecipient: ResultRecipient<ImportMeasureUnitScreenDestination, String>,
) {

    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    val state = viewModel.measureUnits.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()

    val lazyGridState = rememberLazyGridState()

    val showFab = viewModel.totalItems.isNotEmpty()

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    val openDialog = remember { mutableStateOf(false) }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                viewModel.deselectItems()
            }

            is NavResult.Value -> {
                viewModel.deselectItems()
                scope.launch {
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
            navController.navigateUp()
        }
    }

    StandardScaffold(
        navController = navController,
        title = if (selectedItems.isEmpty()) UNIT_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                showScrollToTop = lazyGridState.isScrolled,
                fabText = CREATE_NEW_UNIT,
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navController.navigate(AddEditMeasureUnitScreenDestination())
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
                placeholderText = UNIT_SEARCH_PLACEHOLDER,
                showSettingsIcon = true,
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = {
                    navController.navigate(AddEditMeasureUnitScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {
                    openDialog.value = true
                },
                onSettingsClick = {
                    navController.navigate(MeasureUnitSettingsScreenDestination)
                },
                onSelectAllClick = viewModel::selectAllItems,
                onClearClick = viewModel::clearSearchText,
                onSearchClick = viewModel::openSearchBar,
                onSearchTextChanged = viewModel::searchTextChanged
            )
        },
        fabPosition = if (lazyGridState.isScrolled) FabPosition.End else FabPosition.Center,
        selectionCount = selectedItems.size,
        showBackButton = true,
        onDeselect = viewModel::deselectItems,
        onBackClick = {
            if (showSearchBar) viewModel.closeSearchBar() else navController.navigateUp()
        },
        snackbarHostState = snackbarState,
    ) { _ ->
        Crossfade(
            targetState = state,
            label = "Item State"
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) UNIT_NOT_AVAILABLE else Constants.SEARCH_ITEM_NOT_FOUND,
                        buttonText = CREATE_NEW_UNIT,
                        onClick = {
                            navController.navigate(AddEditMeasureUnitScreenDestination())
                        }
                    )
                }

                is UiState.Success -> {
                    LazyVerticalGrid(
                        modifier = Modifier
                            .padding(SpaceSmall),
                        columns = GridCells.Fixed(2),
                        state = lazyGridState,
                    ) {
                        items(
                            items = state.data,
                            key = { it.unitId }
                        ) { item: MeasureUnit ->
                            MeasureUnitItem(
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


    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
                viewModel.deselectItems()
            },
            title = {
                Text(text = DELETE_ITEM_TITLE)
            },
            text = {
                Text(
                    text = DELETE_ITEM_MESSAGE
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
fun MeasureUnitItem(
    modifier: Modifier = Modifier,
    item: MeasureUnit,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
) {
    val borderStroke = if (doesSelected(item.unitId)) border else null

    ElevatedCard(
        modifier = modifier
            .testTag(UNIT_LIST_ITEM_TAG.plus(item.unitId))
            .padding(SpaceSmall)
            .then(borderStroke?.let {
                Modifier
                    .drawAnimatedBorder(
                        strokeWidth = 1.dp,
                        durationMillis = 2000,
                        shape = CardDefaults.elevatedShape
                    )
            } ?: Modifier)
            .clip(CardDefaults.elevatedShape)
            .combinedClickable(
                onClick = {
                    onClick(item.unitId)
                },
                onLongClick = {
                    onLongClick(item.unitId)
                },
            ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        )
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
                    text = item.unitName,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
                Text(text = item.unitValue.safeString)
            }

            CircularBox(
                icon = Icons.Default.MonitorWeight,
                doesSelected = doesSelected(item.unitId),
            )
        }
    }
}