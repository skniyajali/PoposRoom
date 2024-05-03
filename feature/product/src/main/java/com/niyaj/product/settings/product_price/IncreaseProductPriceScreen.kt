package com.niyaj.product.settings.product_price

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.ProductTestTags
import com.niyaj.common.utils.Constants
import com.niyaj.common.utils.safeString
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.product.components.ProductCard
import com.niyaj.product.destinations.AddEditProductScreenDestination
import com.niyaj.product.settings.ProductSettingsEvent
import com.niyaj.product.settings.ProductSettingsViewModel
import com.niyaj.ui.components.CategoriesData
import com.niyaj.ui.components.InfoText
import com.niyaj.ui.components.ItemNotAvailableHalf
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardScaffoldRouteNew
import com.niyaj.ui.components.StandardSearchBar
import com.niyaj.ui.components.StandardTextField
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.launch

@Destination
@Composable
fun IncreaseProductPriceScreen(
    navigator: DestinationsNavigator,
    viewModel: ProductSettingsViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val lazyRowState = rememberLazyListState()

    val categories = viewModel.categories.collectAsStateWithLifecycle().value
    val products = viewModel.products.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()
    val selectedCategory = viewModel.selectedCategory.toList()

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    val productPrice = viewModel.productPrice.value.safeString

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(data.errorMessage)
                }

                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(data.successMessage)
                }
            }
        }
    }

    fun onBackClick() {
        if (showSearchBar) {
            viewModel.closeSearchBar()
        } else if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        } else {
            navigator.navigateUp()
        }
    }

    BackHandler {
        onBackClick()
    }

    TrackScreenViewEvent(screenName = "Increase Product Price::Screen")

    StandardScaffoldRouteNew(
        title = if (selectedItems.isEmpty()) ProductTestTags.INCREASE_PRODUCTS_TITLE else "${selectedItems.size} Selected",
        showBackButton = selectedItems.isEmpty() || showSearchBar,
        navigationIcon = {
            IconButton(
                onClick = viewModel::deselectItems,
            ) {
                Icon(
                    imageVector = PoposIcons.Close,
                    contentDescription = Constants.CLEAR_ICON,
                )
            }
        },
        showBottomBar = products.isNotEmpty() && lazyListState.isScrollingUp(),
        navActions = {
            if (showSearchBar) {
                StandardSearchBar(
                    searchText = searchText,
                    placeholderText = "Search for products...",
                    onClearClick = viewModel::clearSearchText,
                    onSearchTextChanged = viewModel::searchTextChanged,
                )
            } else {
                if (products.isNotEmpty()) {
                    IconButton(
                        onClick = viewModel::selectAllItems,
                    ) {
                        Icon(
                            imageVector = PoposIcons.Checklist,
                            contentDescription = Constants.SELECT_ALL_ICON,
                        )
                    }

                    IconButton(
                        onClick = viewModel::openSearchBar,
                        modifier = Modifier.testTag(NAV_SEARCH_BTN),
                    ) {
                        Icon(
                            imageVector = PoposIcons.Search,
                            contentDescription = "Search Icon",
                        )
                    }
                }
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceMedium),
                verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            ) {
                InfoText(
                    text = "${
                        if (selectedItems.isEmpty())
                            "All" else "${selectedItems.size}"
                    } products price will be increased.",
                )

                StandardButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(ProductTestTags.INCREASE_PRODUCTS_BTN_TEXT),
                    enabled = productPrice.isNotEmpty(),
                    text = ProductTestTags.INCREASE_PRODUCTS_BTN_TEXT,
                    icon = PoposIcons.AddCircleOutline,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                    ),
                    onClick = {
                        viewModel.onEvent(ProductSettingsEvent.OnIncreaseProductPrice)
                    },
                )
            }
        },
        onBackClick = { onBackClick() },
        fabPosition = FabPosition.End,
        floatingActionButton = {
            ScrollToTop(
                visible = lazyListState.isScrolled,
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            TrackScrollJank(scrollableState = lazyListState, stateName = "Increased Products::List")

            StandardTextField(
                modifier = Modifier.padding(start = SpaceMedium, end = SpaceMedium, top = SpaceSmall),
                value = productPrice,
                label = ProductTestTags.INCREASE_PRODUCTS_TEXT_FIELD,
                leadingIcon = PoposIcons.Rupee,
                keyboardType = KeyboardType.Number,
                onValueChange = {
                    viewModel.onEvent(ProductSettingsEvent.OnChangeProductPrice(it))
                },
            )

            CategoriesData(
                modifier = Modifier.padding(horizontal = SpaceSmall),
                lazyRowState = lazyRowState,
                categories = categories,
                selectedCategory = selectedCategory,
                onSelect = {
                    viewModel.onEvent(ProductSettingsEvent.OnSelectCategory(it))
                },
            )

            LazyColumn(
                state = lazyListState,
                contentPadding = PaddingValues(SpaceSmall),
            ) {
                item {
                    if (products.isEmpty()) {
                        ItemNotAvailableHalf(
                            modifier = Modifier.weight(2f),
                            text = if (searchText.isEmpty()) ProductTestTags.PRODUCT_NOT_AVAIlABLE else ProductTestTags.NO_ITEMS_IN_PRODUCT,
                            buttonText = ProductTestTags.CREATE_NEW_PRODUCT,
                            onClick = {
                                navigator.navigate(AddEditProductScreenDestination())
                            },
                        )
                    }
                }

                itemsIndexed(
                    items = products,
                    key = { index, item ->
                        item.productName.plus(index).plus(item.productId)
                    },
                ) { _, item ->
                    ProductCard(
                        item = item,
                        doesSelected = selectedItems::contains,
                        onClick = viewModel::selectItem,
                        onLongClick = viewModel::selectItem,
                        border = BorderStroke(0.dp, Color.Transparent),
                        showArrow = false,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                }
            }
        }
    }
}