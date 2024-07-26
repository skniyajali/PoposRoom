/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.HomeScreenTestTags
import com.niyaj.common.tags.HomeScreenTestTags.HOME_SEARCH_PLACEHOLDER
import com.niyaj.common.utils.Constants
import com.niyaj.core.ui.R
import com.niyaj.designsystem.components.PoposTonalIconButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.home.components.HomeScreenProducts
import com.niyaj.model.Category
import com.niyaj.model.ProductWithQuantity
import com.niyaj.ui.components.HomeScreenScaffold
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.SelectedOrderBox
import com.niyaj.ui.components.StandardFABIcon
import com.niyaj.ui.components.StandardSearchBar
import com.niyaj.ui.components.TwoColumnLazyRowList
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.CategoryPreviewData
import com.niyaj.ui.parameterProvider.ProductWithQuantityStatePreviewParameter
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Presets
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.OpenResultRecipient
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.PartySystem
import kotlin.random.Random

@RootNavGraph(start = true)
@Destination(route = Screens.HOME_SCREEN)
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    resultRecipient: OpenResultRecipient<String>,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val lazyRowState = rememberLazyStaggeredGridState()
    val snackbarState = remember { SnackbarHostState() }

    val productState by viewModel.productsWithQuantity.collectAsStateWithLifecycle()
    val categoriesState by viewModel.categories.collectAsStateWithLifecycle()
    val selectedOrder by viewModel.selectedId.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val showSearchBar by viewModel.showSearchBar.collectAsStateWithLifecycle()
    val eventFlow by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    val searchText = viewModel.searchText.value
    val selectedId = if (selectedOrder.addressName.isEmpty()) {
        selectedOrder.orderId.toString()
    } else {
        selectedOrder.addressName.plus(" - ").plus(selectedOrder.orderId)
    }

    val showKonfetti = remember { mutableStateOf(false) }

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
//                        snackbarState.showSnackbar(event.successMessage)
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
            viewModel.selectCategory(selectedCategory)
            lazyListState.animateScrollToItem(0)
            lazyRowState.animateScrollToItem(0)
        }
    }

    resultRecipient.onNavResult {
        when (it) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    showKonfetti.value = true
                }
            }
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
        },
        showKonfetti = showKonfetti.value,
        hideKonfetti = { showKonfetti.value = false },
    )
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
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
    showKonfetti: Boolean,
    hideKonfetti: () -> Unit,
    currentRoute: String = Screens.HOME_SCREEN,
    snackbarState: SnackbarHostState = remember { SnackbarHostState() },
    lazyRowState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    lazyListState: LazyListState = rememberLazyListState(),
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val showSearchIcon = productState is UiState.Success
    val animatedColor by animateColorAsState(
        targetValue = if (selectedId != "0") {
            MaterialTheme.colorScheme.tertiaryContainer
        } else {
            Color.Transparent
        },
        label = "",
    )

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
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
                        modifier = Modifier.testTag("drawerButton"),
                        onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        },
                    ) {
                        Icon(
                            imageVector = PoposIcons.App,
                            contentDescription = "app:drawer",
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
                            PoposTonalIconButton(
                                onClick = onCartClick,
                                icon = PoposIcons.OutlinedCart,
                                containerColor = animatedColor,
                                contentColor = MaterialTheme.colorScheme.tertiary,
                            )

                            PoposTonalIconButton(
                                onClick = onOrderClick,
                                icon = PoposIcons.OutlinedOrder,
                                containerColor = animatedColor,
                                contentColor = MaterialTheme.colorScheme.tertiary,
                            )

                            PoposTonalIconButton(
                                onClick = onOpenSearchBar,
                                icon = PoposIcons.Search,
                                containerColor = animatedColor,
                                contentColor = MaterialTheme.colorScheme.tertiary,
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                StandardFABIcon(
                    fabVisible = showSearchIcon && !showSearchBar,
                    onFabClick = {
                        onNavigateToScreen(Screens.ADD_EDIT_CART_ORDER_SCREEN)
                    },
                    fabText = "Create an Order",
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
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                ) {
                    TwoColumnLazyRowList(
                        modifier = Modifier.wrapContentHeight(),
                        lazyRowState = lazyRowState,
                        uiState = categoryState,
                        selectedCategory = selectedCategory,
                        onSelect = onSelectCategory,
                    )

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
                            HomeScreenProducts(
                                lazyListState = lazyListState,
                                products = productState.data,
                                onIncrease = onIncreaseQuantity,
                                onDecrease = onDecreaseQuantity,
                                onCreateProduct = onClickCreateProduct,
                            )
                        }
                    }
                }
            }
        }

        ScrollToTop(
            modifier = Modifier
                .padding(bottom = 40.dp, end = 24.dp)
                .align(Alignment.BottomEnd),
            visible = lazyListState.isScrolled,
            onClick = onClickScrollToTop,
        )

        if (showKonfetti) {
            val parties = if (Random.nextBoolean()) Presets.rain() else Presets.parade()

            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = parties,
                updateListener = object : OnParticleSystemUpdateListener {
                    override fun onParticleSystemEnded(
                        system: PartySystem,
                        activeSystems: Int,
                    ) {
                        if (activeSystems == 0) hideKonfetti()
                    }
                },
            )
        }
    }
}

@DevicePreviews
@Composable
private fun HomeScreenContentPreview(
    @PreviewParameter(ProductWithQuantityStatePreviewParameter::class)
    productState: UiState<ImmutableList<ProductWithQuantity>>,
    modifier: Modifier = Modifier,
    categoryList: ImmutableList<Category> = CategoryPreviewData.categoryList.toImmutableList(),
) {
    PoposRoomTheme {
        HomeScreenContent(
            modifier = modifier,
            productState = productState,
            categoryState = UiState.Success(categoryList),
            selectedCategory = 0,
            selectedId = "0",
            showSearchBar = false,
            searchText = "",
            onOpenSearchBar = {},
            onCloseSearchBar = {},
            onSearchTextChanged = {},
            onClearClick = {},
            onNavigateToScreen = {},
            onSelectCategory = {},
            onIncreaseQuantity = {},
            onDecreaseQuantity = {},
            onClickScrollToTop = {},
            onClickCreateProduct = {},
            onCartClick = {},
            onOrderClick = {},
            showKonfetti = false,
            hideKonfetti = {},
        )
    }
}
