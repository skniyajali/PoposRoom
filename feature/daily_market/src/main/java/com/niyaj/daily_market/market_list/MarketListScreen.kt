package com.niyaj.daily_market.market_list

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.MarketListTestTags
import com.niyaj.common.tags.MarketListTestTags.MARKET_ITEM_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.MarketListTestTags.MARKET_LIST_SCREEN_TITLE
import com.niyaj.common.utils.Constants
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toTimeSpan
import com.niyaj.daily_market.destinations.AddEditMarketItemScreenDestination
import com.niyaj.daily_market.destinations.AddEditMarketListScreenDestination
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.MarketList
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.StandardScaffold
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@Composable
@Destination
fun MarketListScreen(
    navController: NavController,
    viewModel: MarketListViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditMarketListScreenDestination, String>
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    val state = viewModel.items.collectAsStateWithLifecycle().value

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
                showSearchIcon = true,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = {
                    navController.navigate(AddEditMarketItemScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {

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
                                marketList = items.marketList,
                                onClickList = {
                                    navController.navigate(AddEditMarketListScreenDestination(it))
                                }
                            )

                            Spacer(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarketListItemCard(
    marketList: MarketList,
    onClickList: (marketId: Int) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ListItem(
                headlineContent = {
                    Text(
                        text = marketList.marketDate.toPrettyDate(),
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
                        Text(
                            text = marketList.createdAt.toTimeSpan,
                            style = MaterialTheme.typography.labelSmall,
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                            contentDescription = "View Details"
                        )
                    }
                },
                modifier = Modifier.clickable {
                    onClickList(marketList.marketId)
                }
            )
        }
    }
}