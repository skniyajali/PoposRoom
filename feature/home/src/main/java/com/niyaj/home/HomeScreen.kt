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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.HomeScreenTestTags
import com.niyaj.common.tags.HomeScreenTestTags.HOME_SEARCH_PLACEHOLDER
import com.niyaj.common.utils.Constants
import com.niyaj.core.ui.R
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.home.components.HomeScreenProducts
import com.niyaj.model.Category
import com.niyaj.model.ProductWithQuantity
import com.niyaj.ui.components.CategoriesData
import com.niyaj.ui.components.HomeScreenScaffold
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.SelectedOrderBox
import com.niyaj.ui.components.StandardFABIcon
import com.niyaj.ui.components.StandardFilledTonalIconButton
import com.niyaj.ui.components.StandardSearchBar
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination(route = Screens.HOME_SCREEN)
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
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
            navigator.popBackStack()
        }
    }

    BackHandler(onBack = onBackClick)

    HomeScreenContent(
        snackbarState = snackbarState,
        lazyRowState = lazyRowState,
        lazyListState = lazyListState,
        productState = productState,
        categoryState = categoriesState,
        selectedCategory = selectedCategory,
        selectedId = selectedId,
        showSearchBar = showSearchBar,
        searchText = searchText,
        onOpenSearchBar = viewModel::openSearchBar,
        onCloseSearchBar = viewModel::closeSearchBar,
        onSearchTextChanged = viewModel::searchTextChanged,
        onClearClick = viewModel::clearSearchText,
        onNavigateToScreen = navigator::navigate,
        onSelectCategory = viewModel::selectCategory,
        onIncreaseQuantity = {
            if (selectedOrder.orderId != 0) {
                viewModel.addProductToCart(selectedOrder.orderId, it)
            } else {
                navigator.navigate(Screens.ADD_EDIT_CART_ORDER_SCREEN)
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
            navigator.navigate(Screens.ADD_EDIT_PRODUCT_SCREEN)
        },
        onCartClick = {
            navigator.navigate(Screens.CART_SCREEN)
        },
        onOrderClick = {
            navigator.navigate(Screens.ORDER_SCREEN)
        }
    )
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    snackbarState: SnackbarHostState,
    lazyRowState: LazyListState,
    lazyListState: LazyListState,
    productState: UiState<ImmutableList<ProductWithQuantity>>,
    categoryState: UiState<ImmutableList<Category>>,
    selectedCategory: Int,
    selectedId: String,
    showSearchBar: Boolean,
    searchText: String,
    onOpenSearchBar: () -> Unit,
    onCloseSearchBar: () -> Unit,
    onSearchTextChanged: (String) -> Unit,
    onClearClick: () -> Unit,
    onNavigateToScreen: (String) -> Unit,
    onSelectCategory: (Int) -> Unit,
    onIncreaseQuantity: (Int) -> Unit,
    onDecreaseQuantity: (Int) -> Unit,
    onClickScrollToTop: () -> Unit,
    onClickCreateProduct: () -> Unit,
    onCartClick: () -> Unit,
    onOrderClick: () -> Unit,
    currentRoute: String = Screens.HOME_SCREEN,
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val showSearchIcon = productState is UiState.Success
    val showFab = showSearchIcon && selectedCategory == 0

    HomeScreenScaffold(
        modifier = modifier,
        currentRoute = currentRoute,
        drawerState = drawerState,
        onNavigateToScreen = onNavigateToScreen,
        snackbarHostState = snackbarState,
        title = {
            if (selectedId != "0" && !showSearchBar) {
                SelectedOrderBox(
                    modifier = Modifier
                        .padding(horizontal = SpaceMedium),
                    text = selectedId,
                    height = 40.dp,
                    onClick = {
                        onNavigateToScreen(Screens.SELECT_ORDER_SCREEN)
                    },
                )
            }
        },
        navigationIcon = {
            if (showSearchBar) {
                IconButton(
                    onClick = onCloseSearchBar,
                    modifier = Modifier.testTag(Constants.STANDARD_BACK_BUTTON),
                ) {
                    Icon(
                        imageVector = PoposIcons.Back,
                        contentDescription = Constants.STANDARD_BACK_BUTTON,
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                ) {
                    Icon(
                        imageVector = PoposIcons.App,
                        contentDescription = null,
                    )
                }
            }
        },
        navActions = {
            if (showSearchBar) {
                StandardSearchBar(
                    searchText = searchText,
                    placeholderText = HOME_SEARCH_PLACEHOLDER,
                    onClearClick = onClearClick,
                    onSearchTextChanged = onSearchTextChanged,
                )
            } else {
                AnimatedVisibility(showSearchIcon) {
                    Row {
                        StandardFilledTonalIconButton(
                            onClick = onCartClick,
                            icon = PoposIcons.OutlinedCart,
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.tertiary
                        )

                        StandardFilledTonalIconButton(
                            onClick = onOrderClick,
                            icon = PoposIcons.OutlinedOrder,
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.tertiary

                        )

                        StandardFilledTonalIconButton(
                            onClick = onOpenSearchBar,
                            icon = PoposIcons.Search,
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            StandardFABIcon(
                fabVisible = showFab,
                onFabClick = {
                    onNavigateToScreen(Screens.ADD_EDIT_CART_ORDER_SCREEN)
                },
                onClickScroll = onClickScrollToTop,
                showScrollToTop = !lazyListState.isScrollingUp(),
                fabText = "Create an Order",
                scrollText = "Scroll to Top",
            )
        },
        fabPosition = FabPosition.Center,
    ) { padding ->
        ElevatedCard(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            elevation = CardDefaults.cardElevation(),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            when (productState) {
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
                            products = productState.data,
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