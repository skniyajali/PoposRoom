package com.niyaj.poposroom.features.main_feed.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.poposroom.R
import com.niyaj.poposroom.features.common.components.CircularBoxWithQty
import com.niyaj.poposroom.features.common.components.ItemNotAvailable
import com.niyaj.poposroom.features.common.components.LoadingIndicator
import com.niyaj.poposroom.features.common.components.StandardScaffoldWithBottomNavigation
import com.niyaj.poposroom.features.common.components.TitleWithIcon
import com.niyaj.poposroom.features.common.event.UiState
import com.niyaj.poposroom.features.common.ui.theme.SpaceMini
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.common.utils.isScrolled
import com.niyaj.poposroom.features.common.utils.toRupee
import com.niyaj.poposroom.features.destinations.AddEditProductScreenDestination
import com.niyaj.poposroom.features.main_feed.domain.utils.MainFeedTestTags
import com.niyaj.poposroom.features.product.domain.model.Product
import com.niyaj.poposroom.features.product.domain.utils.ProductTestTags
import com.niyaj.poposroom.features.product.presentation.CategoriesData
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.launch

@Destination
@RootNavGraph(start = true)
@Composable
fun MainFeedScreen(
    navController: NavController,
    viewModel: MainFeedViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    val snackbarState = remember { SnackbarHostState() }
    val state = viewModel.products.collectAsStateWithLifecycle().value

    val selectedId = viewModel.selectedId.collectAsStateWithLifecycle().value
    val selectedCategory = viewModel.selectedCategory.collectAsStateWithLifecycle().value

    val showFab = viewModel.totalItems.isNotEmpty() && selectedCategory == 0

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    val categories = viewModel.categories.collectAsStateWithLifecycle().value

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
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

    LaunchedEffect(key1 = selectedCategory) {
        scope.launch {
            lazyListState.animateScrollToItem(0)
        }
    }

    BackHandler {
        navController.popBackStack()
    }

    StandardScaffoldWithBottomNavigation(
        navController = navController,
        selectedId = selectedId.toString(),
        showFab = showFab && !lazyListState.isScrolled,
        isScrolling = lazyListState.isScrolled,
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
                            navController.navigate(AddEditProductScreenDestination())
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
                            categories = categories,
                            selectedCategory = selectedCategory,
                            onSelect = viewModel::selectCategory
                        )

                        MainFeedProducts(
                            lazyListState = lazyListState,
                            products = state.data,
                            productQty = { 0 },
                            onIncrease = {},
                            onDecrease = {},
                            onCreateProduct = {
                                navController.navigate(AddEditProductScreenDestination())
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
    products: List<Product>,
    productQty: (Int) -> Int,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
    onCreateProduct: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
    ) {
        TitleWithIcon(
            text = "Products",
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
                    productQty = productQty,
                    onIncrease = onIncrease,
                    onDecrease = onDecrease
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Could not find what you were looking for?",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    TextButton(
                        onClick = onCreateProduct,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "")

                        Spacer(modifier = Modifier.width(SpaceMini))

                        Text(text = MainFeedTestTags.CREATE_NEW_PRODUCT)
                    }
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}

@Composable
fun MainFeedProductData(
    modifier: Modifier = Modifier,
    product: Product,
    productQty: (Int) -> Int,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
) {
    ListItem(
        modifier = modifier
            .testTag(ProductTestTags.PRODUCT_TAG.plus(product.productId))
            .fillMaxWidth()
            .clip(RoundedCornerShape(SpaceMini)),
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
                qty = productQty(product.productId)
            )
        },
        trailingContent = {
            IncDecBox(
                onDecrease = { onDecrease(product.productId) },
                onIncrease = { onIncrease(product.productId) },
            )
        }
    )
}

@Composable
fun IncDecBox(
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .height(40.dp),
        shape = RoundedCornerShape(SpaceMini),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable {
                        onDecrease()
                    }
                    .padding(SpaceSmall),
            ) {
                Spacer(modifier = Modifier.width(SpaceSmall))
                Icon(imageVector = Icons.Default.Remove, contentDescription = "remove")
                Spacer(modifier = Modifier.width(SpaceSmall))
            }
            
            Divider(modifier = Modifier
                .width(1.dp)
                .fillMaxHeight())

            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable {
                        onIncrease()
                    }
                    .padding(SpaceSmall),
            ) {
                Spacer(modifier = Modifier.width(SpaceSmall))
                Icon(imageVector = Icons.Default.Add, contentDescription = "add")
                Spacer(modifier = Modifier.width(SpaceSmall))
            }
        }
    }

}