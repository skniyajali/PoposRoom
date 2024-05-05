/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
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
import com.niyaj.model.Category
import com.niyaj.model.ProductWithQuantity
import com.niyaj.ui.components.CategoriesData
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardScaffoldWithBottomNavigation
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import kotlinx.collections.immutable.ImmutableList
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

    val productState = viewModel.productsWithQuantity.collectAsStateWithLifecycle().value
    val categoriesState = viewModel.categories.collectAsStateWithLifecycle().value

    val selectedOrder = viewModel.selectedId.collectAsStateWithLifecycle().value
    val selectedId = if (selectedOrder.addressName.isEmpty()) {
        selectedOrder.orderId.toString()
    } else {
        selectedOrder.addressName.plus(" - ").plus(selectedOrder.orderId)
    }
    val selectedCategory = viewModel.selectedCategory.collectAsStateWithLifecycle().value
    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    val doesStateIsEmpty = productState is UiState.Empty

    val showFab = !doesStateIsEmpty && selectedCategory == 0 && lazyListState.isScrollingUp()

    val eventFlow = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    LaunchedEffect(key1 = eventFlow) {
        eventFlow?.let { event ->
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

    val onBackClick: () -> Unit = {
        if (showSearchBar) {
            viewModel.closeSearchBar()
        } else if (selectedCategory != 0) {
            viewModel.selectCategory(selectedCategory)
        } else {
            navController.popBackStack()
        }
    }

    BackHandler(onBack = onBackClick)

    HomeScreenContent(
        snackbarState = snackbarState,
        lazyRowState = lazyRowState,
        lazyListState = lazyListState,
        selectedId = selectedId,
        showFab = showFab,
        showSearchIcon = !doesStateIsEmpty,
        showSearchBar = showSearchBar,
        searchText = searchText,
        onOpenSearchBar = viewModel::openSearchBar,
        onCloseSearchBar = viewModel::closeSearchBar,
        onSearchTextChanged = viewModel::searchTextChanged,
        onClearClick = viewModel::clearSearchText,
        onBackClick = onBackClick,
        onNavigateToScreen = navController::navigate,
        productState = productState,
        categoryState = categoriesState,
        selectedCategory = selectedCategory,
        onSelectCategory = viewModel::selectCategory,
        onIncreaseQuantity = {
            if (selectedOrder.orderId != 0) {
                viewModel.addProductToCart(selectedOrder.orderId, it)
            } else {
                navController.navigate(Screens.ADD_EDIT_CART_ORDER_SCREEN)
            }
        },
        onDecreaseQuantity = {
            viewModel.removeProductFromCart(selectedOrder.orderId, it)
        },
        onClickScrollToTop = {
            scope.launch {
                lazyListState.animateScrollToItem(0)
            }
        },
        onClickCreateProduct = {
            navController.navigate(Screens.ADD_EDIT_PRODUCT_SCREEN)
        },
    )
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    snackbarState: SnackbarHostState,
    lazyRowState: LazyListState,
    lazyListState: LazyListState,
    selectedId: String,
    showFab: Boolean,
    showBottomBar: Boolean = showFab,
    showSearchIcon: Boolean,
    showSearchBar: Boolean,
    searchText: String,
    onOpenSearchBar: () -> Unit,
    onCloseSearchBar: () -> Unit,
    onSearchTextChanged: (String) -> Unit,
    onClearClick: () -> Unit,
    onBackClick: () -> Unit,
    onNavigateToScreen: (String) -> Unit,
    productState: UiState<ImmutableList<ProductWithQuantity>>,
    categoryState: UiState<ImmutableList<Category>>,
    selectedCategory: Int,
    onSelectCategory: (Int) -> Unit,
    onIncreaseQuantity: (Int) -> Unit,
    onDecreaseQuantity: (Int) -> Unit,
    onClickScrollToTop: () -> Unit,
    onClickCreateProduct: () -> Unit,
    currentRoute: String = Screens.HOME_SCREEN,
) {
    StandardScaffoldWithBottomNavigation(
        currentRoute = currentRoute,
        snackbarHostState = snackbarState,
        selectedId = selectedId,
        showFab = showFab,
        showBottomBar = showBottomBar,
        showSearchIcon = showSearchIcon,
        showSearchBar = showSearchBar,
        searchText = searchText,
        openSearchBar = onOpenSearchBar,
        closeSearchBar = onCloseSearchBar,
        onSearchTextChanged = onSearchTextChanged,
        onClearClick = onClearClick,
        searchPlaceholderText = HOME_SEARCH_PLACEHOLDER,
        showBackButton = showSearchBar,
        onBackClick = onBackClick,
        onNavigateToScreen = onNavigateToScreen,
    ) {
        Crossfade(
            targetState = productState,
            label = "MainFeedState",
            modifier = modifier
                .fillMaxSize()
                .padding(SpaceSmall),
        ) { state ->
            when (state) {
                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = HomeScreenTestTags.PRODUCT_NOT_AVAILABLE,
                        image = painterResource(id = R.drawable.nothinghere),
                        buttonText = HomeScreenTestTags.CREATE_NEW_PRODUCT,
                        onClick = onClickCreateProduct,
                    )
                }

                is UiState.Loading -> LoadingIndicator()

                is UiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        CategoriesData(
                            lazyRowState = lazyRowState,
                            uiState = categoryState,
                            selectedCategory = selectedCategory,
                            onSelect = onSelectCategory,
                        )

                        HomeScreenProducts(
                            lazyListState = lazyListState,
                            products = state.data,
                            onIncrease = onIncreaseQuantity,
                            onDecrease = onDecreaseQuantity,
                            onCreateProduct = onClickCreateProduct,
                            onClickScrollToTop = onClickScrollToTop,
                        )
                    }
                }
            }
        }
    }
}