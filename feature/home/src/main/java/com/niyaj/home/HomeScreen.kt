package com.niyaj.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.MainFeedTestTags
import com.niyaj.common.tags.ProductTestTags
import com.niyaj.common.utils.toRupee
import com.niyaj.core.ui.R
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.ProductWithFlowQuantity
import com.niyaj.ui.components.CategoriesData
import com.niyaj.ui.components.CircularBoxWithQty
import com.niyaj.ui.components.IncDecBox
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.ItemNotFound
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardScaffoldWithBottomNavigation
import com.niyaj.ui.components.TitleWithIcon
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
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
    viewModel: MainFeedViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val lazyRowState = rememberLazyListState()

    val snackbarState = remember { SnackbarHostState() }
    val state = viewModel.products.collectAsStateWithLifecycle().value

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

    BackHandler {
        if (showSearchBar) {
            viewModel.closeSearchBar()
        }else {
            navController.popBackStack()
        }
    }

    StandardScaffoldWithBottomNavigation(
        navController = navController,
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
                        text = MainFeedTestTags.PRODUCT_NOT_AVAIlABLE,
                        image = painterResource(id = R.drawable.nothinghere),
                        buttonText = MainFeedTestTags.CREATE_NEW_PRODUCT,
                        onClick = {
                            navController.navigate(Screens.ADD_EDIT_PRODUCT_SCREEN)
                        }
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
                            onSelect = viewModel::selectCategory
                        )

                        MainFeedProducts(
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
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainFeedProducts(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    products: List<ProductWithFlowQuantity>,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
    onCreateProduct: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
        verticalArrangement = Arrangement.spacedBy(SpaceSmall)
    ) {
        TitleWithIcon(
            text = "Products",
            icon = Icons.Default.Dns,
            showScrollToTop = lazyListState.isScrolled,
            onClickScrollToTop = {
                scope.launch {
                    lazyListState.animateScrollToItem(0)
                }
            }
        )

        LazyColumn(
            state = lazyListState,
        ) {
            items(
                items = products,
                key = {
                    it.productId
                }
            ) { product ->
                MainFeedProductData(
                    product = product,
                    onIncrease = onIncrease,
                    onDecrease = onDecrease
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item {
                ItemNotFound(
                    btnText = MainFeedTestTags.CREATE_NEW_PRODUCT,
                    onBtnClick = onCreateProduct
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}

@Composable
fun MainFeedProductData(
    modifier: Modifier = Modifier,
    product: ProductWithFlowQuantity,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
) {
    val productQty = product.quantity.collectAsStateWithLifecycle(initialValue = 0).value

    ListItem(
        modifier = modifier
            .testTag(ProductTestTags.PRODUCT_TAG.plus(product.productId))
            .fillMaxWidth()
            .clip(RoundedCornerShape(SpaceMini)),
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        ),
        headlineContent = {
            Text(
                text = product.productName,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Text(text = product.productPrice.toRupee)
        },
        leadingContent = {
            CircularBoxWithQty(
                text = product.productName,
                qty = productQty
            )
        },
        trailingContent = {
            IncDecBox(
                enableDecreasing = productQty > 0,
                onDecrease = { onDecrease(product.productId) },
                onIncrease = { onIncrease(product.productId) },
            )
        }
    )
}
