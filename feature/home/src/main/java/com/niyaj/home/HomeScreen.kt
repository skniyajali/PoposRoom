package com.niyaj.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.HomeScreenTestTags
import com.niyaj.common.tags.HomeScreenTestTags.HOME_SEARCH_PLACEHOLDER
import com.niyaj.core.ui.R
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.home.components.HomeScreenProducts
import com.niyaj.ui.components.CategoriesData
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardScaffoldWithBottomNavigation
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.currentRoute
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination(route = Screens.HOME_SCREEN)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val lazyRowState = rememberLazyListState()

    val snackbarState = remember { SnackbarHostState() }
    val state = viewModel.productsWithQuantity.collectAsStateWithLifecycle().value

    val categories = viewModel.categories.collectAsStateWithLifecycle().value

    val selectedOrder = viewModel.selectedId.collectAsStateWithLifecycle().value
    val selectedId = if (selectedOrder.addressName.isEmpty()) {
        selectedOrder.orderId.toString()
    } else {
        selectedOrder.addressName.plus(" - ").plus(selectedOrder.orderId)
    }
    val selectedCategory = viewModel.selectedCategory.collectAsStateWithLifecycle().value

    val totalItems = viewModel.totalItems

    val showFab = totalItems.isNotEmpty() && selectedCategory == 0 && lazyListState.isScrollingUp()

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.distinctUntilChanged().collectLatest { event ->
            when (event) {
                is UiEvent.OnError -> {
                    scope.launch {
                        snackbarState.showSnackbar(event.errorMessage)
                    }
                }

                is UiEvent.OnSuccess -> {
                    scope.launch {
                        snackbarState.showSnackbar(event.successMessage)
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = selectedCategory) {
        scope.launch {
            lazyListState.animateScrollToItem(0)
        }
    }

    LaunchedEffect(key1 = selectedOrder.orderId) {
        scope.launch {
            lazyListState.animateScrollToItem(0)
            lazyRowState.animateScrollToItem(0)
        }
    }

    TrackScreenViewEvent(screenName = Screens.HOME_SCREEN)

    BackHandler {
        if (showSearchBar) {
            viewModel.closeSearchBar()
        } else {
            navController.popBackStack()
        }
    }

    StandardScaffoldWithBottomNavigation(
        currentRoute = navController.currentRoute(),
        snackbarHostState = snackbarState,
        selectedId = selectedId,
        showFab = showFab,
        showBottomBar = showFab,
        showSearchIcon = totalItems.isNotEmpty(),
        showSearchBar = showSearchBar,
        searchText = searchText,
        openSearchBar = viewModel::openSearchBar,
        closeSearchBar = viewModel::closeSearchBar,
        onSearchTextChanged = viewModel::searchTextChanged,
        onClearClick = viewModel::clearSearchText,
        searchPlaceholderText = HOME_SEARCH_PLACEHOLDER,
        showBackButton = showSearchBar,
        onBackClick = { navController.navigateUp() },
        onNavigateToScreen = { navController.navigate(it) },
    ) {
        Crossfade(
            targetState = state,
            label = "MainFeedState",
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
        ) { state ->
            when (state) {
                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = HomeScreenTestTags.PRODUCT_NOT_AVAILABLE,
                        image = painterResource(id = R.drawable.nothinghere),
                        buttonText = HomeScreenTestTags.CREATE_NEW_PRODUCT,
                        onClick = {
                            navController.navigate(Screens.ADD_EDIT_PRODUCT_SCREEN)
                        },
                    )
                }

                is UiState.Loading -> LoadingIndicator()

                is UiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        CategoriesData(
                            lazyRowState = lazyRowState,
                            categories = categories,
                            selectedCategory = selectedCategory,
                            onSelect = viewModel::selectCategory,
                        )

                        HomeScreenProducts(
                            lazyListState = lazyListState,
                            products = state.data,
                            onIncrease = {
                                if (selectedOrder.orderId != 0) {
                                    viewModel.addProductToCart(selectedOrder.orderId, it)
                                } else {
                                    navController.navigate(Screens.ADD_EDIT_CART_ORDER_SCREEN)
                                }
                            },
                            onDecrease = {
                                viewModel.removeProductFromCart(selectedOrder.orderId, it)
                            },
                            onCreateProduct = {
                                navController.navigate(Screens.ADD_EDIT_PRODUCT_SCREEN)
                            },
                        )
                    }
                }
            }
        }
    }
}