package com.niyaj.daily_market.market_list

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FabPosition
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.MarketListTestTags
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.MarketListTestTags.MARKET_LIST_SCREEN_TITLE
import com.niyaj.common.utils.Constants
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toTimeSpan
import com.niyaj.daily_market.destinations.AddEditMarketListScreenDestination
import com.niyaj.daily_market.market_list.add_edit.ShareableMarketList
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.MarketListWithItems
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.StandardFilledTonalIconButton
import com.niyaj.ui.components.StandardScaffold
import com.niyaj.ui.components.TextWithIcon
import com.niyaj.ui.components.drawAnimatedBorder
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens.MARKET_LIST_SCREEN
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
import com.niyaj.ui.utils.rememberCaptureController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.launch

@Composable
@Destination(route = MARKET_LIST_SCREEN)
fun MarketListScreen(
    navController: NavController,
    viewModel: MarketListViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    val state = viewModel.items.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    val captureController = rememberCaptureController()

    val selectedItems = viewModel.selectedItems.toList()

    val lazyGridState = rememberLazyGridState()

    val showFab = viewModel.totalItems.isNotEmpty()

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    val openDialog = remember { mutableStateOf(false) }
    val showList = viewModel.showList.collectAsStateWithLifecycle().value
    val marketLists = viewModel.listItems.collectAsStateWithLifecycle().value

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
        title = if (selectedItems.isEmpty()) MARKET_LIST_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                showScrollToTop = lazyGridState.isScrolled,
                fabText = MarketListTestTags.CREATE_NEW_LIST,
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = viewModel::createNewList,
                onClickScroll = {
                    scope.launch {
                        lazyGridState.animateScrollToItem(0)
                    }
                }
            )
        },
        navActions = {
            ScaffoldNavActions(
                placeholderText = MARKET_ITEM_SEARCH_PLACEHOLDER,
                showSettingsIcon = true,
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = {
                    navController.navigate(AddEditMarketListScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {
                    openDialog.value = true
                },
                onSettingsClick = {
//                    navController.navigate(AddEditMarketItemScreenDestination())
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
    ) {
        Crossfade(
            targetState = state,
            label = "Item State"
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) MarketListTestTags.MARKET_ITEM_NOT_AVAILABLE else Constants.SEARCH_ITEM_NOT_FOUND,
                        buttonText = MarketListTestTags.CREATE_NEW_LIST,
                        onClick = viewModel::createNewList
                    )
                }

                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(SpaceSmall)
                    ) {
                        items(
                            items = state.data,
                            key = {
                                it.marketList.marketId
                            }
                        ) { items ->
                            MarketListItemCard(
                                withItems = items,
                                doesSelected = {
                                    selectedItems.contains(it)
                                },
                                onClick = {
                                    if (selectedItems.isEmpty()) {
                                        navController.navigate(AddEditMarketListScreenDestination(it))
                                    } else {
                                        viewModel.selectItem(it)
                                    }
                                },
                                onLongClick = viewModel::selectItem,
                                onClickShare = viewModel::onShowList,
                                onClickPrint = {
                                    // TODO:: print market list
                                },
                            )

                            Spacer(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
    }


    AnimatedVisibility(
        visible = showList != 0L && marketLists.isNotEmpty()
    ) {
        ShareableMarketList(
            captureController = captureController,
            marketDate = showList,
            onDismiss = viewModel::onDismissList,
            marketLists = marketLists,
            onClickShare = {
                captureController.captureLongScreenshot()
            },
            onCaptured = { bitmap, error ->
                bitmap?.let {
                    scope.launch {
                        val uri = viewModel.saveImage(it, context)
                        uri?.let {
                            viewModel.shareContent(context, "Share Image", uri)
                        }
                    }
                }
                error?.let {
                    Log.d("Capturable", "Error: ${it.message}\n${it.stackTrace.joinToString()}")
                }
            },
        )
    }


    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
                viewModel.deselectItems()
            },
            title = {
                Text(text = MarketListTestTags.DELETE_LIST_TITLE)
            },
            text = {
                Text(
                    text = MarketListTestTags.DELETE_LIST_MESSAGE
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
fun MarketListItemCard(
    withItems: MarketListWithItems,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    onClickShare: (Int, Long) -> Unit,
    onClickPrint: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
) {
    val marketId = withItems.marketList.marketId
    val borderStroke = if (doesSelected(marketId)) border else null

    ElevatedCard(
        modifier = Modifier
            .testTag(MarketListTestTags.MARKET_LIST_ITEM_TAG.plus(marketId))
            .fillMaxWidth()
            .padding(SpaceSmall)
            .then(borderStroke?.let {
                Modifier
                    .drawAnimatedBorder(
                        strokeWidth = 1.dp,
                        durationMillis = 2000,
                        shape = CardDefaults.elevatedShape
                    )
            } ?: Modifier)
            .clip(CardDefaults.elevatedShape),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            ListItem(
                headlineContent = {
                    Text(
                        text = withItems.marketList.marketDate.toPrettyDate(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                },
                leadingContent = {
                    CircularBox(
                        icon = Icons.Default.ShoppingBag,
                        doesSelected = false
                    )
                },
                trailingContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            SpaceSmall,
                            Alignment.CenterHorizontally
                        )
                    ) {
                        StandardFilledTonalIconButton(
                            icon = Icons.Default.Print,
                            onClick = {
                                onClickPrint(marketId)
                            },
                            enabled = withItems.items.isNotEmpty(),
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )

                        StandardFilledTonalIconButton(
                            icon = Icons.Default.Share,
                            onClick = {
                                onClickShare(marketId, withItems.marketList.marketDate)
                            },
                            enabled = withItems.items.isNotEmpty(),
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    }
                },
                modifier = Modifier.combinedClickable(
                    onClick = {
                        onClick(marketId)
                    },
                    onLongClick = {
                        onLongClick(marketId)
                    },
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconWithText(
                    text = "${withItems.items.size} Items",
                    icon = Icons.Default.Inbox
                )

                TextWithIcon(
                    text = (withItems.marketList.updatedAt
                        ?: withItems.marketList.createdAt).toTimeSpan,
                    icon = Icons.Default.AccessTime,
                    tintColor = Color.Gray,
                    textColor = Color.Gray
                )
            }
        }
    }
}