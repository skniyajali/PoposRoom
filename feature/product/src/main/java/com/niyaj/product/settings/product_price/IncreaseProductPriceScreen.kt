package com.niyaj.product.settings.product_price

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.ProductTestTags
import com.niyaj.common.utils.Constants
import com.niyaj.common.utils.safeString
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
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
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.components.StandardSearchBar
import com.niyaj.ui.components.StandardTextField
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.launch

@Destination
@Composable
fun IncreaseProductPriceScreen(
    navController: NavController,
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

    val selectionCount = rememberUpdatedState(newValue = selectedItems.size)

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
        }  else {
            navController.navigateUp()
        }
    }

    BackHandler {
        onBackClick()
    }

    TrackScreenViewEvent(screenName = "Increase Product Price::Screen")
    
    StandardScaffoldNew(
        navController = navController,
        title = if (selectedItems.isEmpty()) ProductTestTags.INCREASE_PRODUCTS_TITLE else "${selectedItems.size} Selected",
        navigationIcon = {
            AnimatedContent(
                targetState = selectionCount,
                transitionSpec = {
                    (fadeIn()).togetherWith(
                        fadeOut(animationSpec = tween(200))
                    )
                },
                label = "navigationIcon",
                contentKey = {
                    it
                }
            ) { intState ->
                if (intState.value != 0 && !showSearchBar) {
                    IconButton(
                        onClick = viewModel::deselectItems
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = Constants.CLEAR_ICON
                        )
                    }
                } else {
                    IconButton(
                        onClick = { onBackClick() },
                        modifier = Modifier.testTag(Constants.STANDARD_BACK_BUTTON)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                }
            }
        },
        showDrawer = false,
        showBottomBar = products.isNotEmpty() && lazyListState.isScrollingUp(),
        navActions = {
            if (showSearchBar) {
                StandardSearchBar(
                    searchText = searchText,
                    placeholderText = "Search for products...",
                    onClearClick = viewModel::clearSearchText,
                    onSearchTextChanged = viewModel::searchTextChanged
                )
            } else {
                if (products.isNotEmpty()) {
                    IconButton(
                        onClick = viewModel::selectAllItems
                    ) {
                        Icon(
                            imageVector = Icons.Default.Checklist,
                            contentDescription = Constants.SELECT_ALL_ICON
                        )
                    }

                    IconButton(
                        onClick = viewModel::openSearchBar,
                        modifier = Modifier.testTag(NAV_SEARCH_BTN)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
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
                    .padding(SpaceSmallMax),
                verticalArrangement = Arrangement.spacedBy(SpaceSmall)
            ) {
                InfoText(
                    text = "${
                        if (selectedItems.isEmpty())
                            "All" else "${selectedItems.size}"
                    } products price will be increased."
                )

                StandardButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(ProductTestTags.INCREASE_PRODUCTS_BTN_TEXT),
                    enabled = productPrice.isNotEmpty(),
                    text = ProductTestTags.INCREASE_PRODUCTS_BTN_TEXT,
                    icon = Icons.Default.AddCircleOutline,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    onClick = {
                        viewModel.onEvent(ProductSettingsEvent.OnIncreaseProductPrice)
                    }
                )
            }
        },
        onBackClick = { onBackClick() },
        fabPosition = FabPosition.End,
        floatingActionButton = {
            ScrollToTop(
                visible = !lazyListState.isScrollingUp(),
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
        ) {
            TrackScrollJank(scrollableState = lazyListState, stateName = "Increased Products::List")

            StandardTextField(
                modifier = Modifier.padding(horizontal = SpaceSmall),
                value = productPrice,
                label = ProductTestTags.INCREASE_PRODUCTS_TEXT_FIELD,
                leadingIcon = Icons.Default.CurrencyRupee,
                keyboardType = KeyboardType.Number,
                onValueChange = {
                    viewModel.onEvent(ProductSettingsEvent.OnChangeProductPrice(it))
                }
            )

            CategoriesData(
                lazyRowState = lazyRowState,
                categories = categories,
                selectedCategory = selectedCategory,
                onSelect = {
                    viewModel.onEvent(ProductSettingsEvent.OnSelectCategory(it))
                }
            )

            LazyColumn(
                state = lazyListState,
            ) {
                item {
                    if (products.isEmpty()) {
                        ItemNotAvailableHalf(
                            modifier = Modifier.weight(2f),
                            text = if (searchText.isEmpty()) ProductTestTags.PRODUCT_NOT_AVAIlABLE else ProductTestTags.NO_ITEMS_IN_PRODUCT,
                            buttonText = ProductTestTags.CREATE_NEW_PRODUCT,
                            onClick = {
                                navController.navigate(AddEditProductScreenDestination())
                            }
                        )
                    }
                }
                itemsIndexed(
                    items = products,
                    key = { index, item ->
                        item.productName.plus(index).plus(item.productId)
                    }
                ) { _, item ->
                    ProductCard(
                        item = item,
                        doesSelected = {
                            selectedItems.contains(it)
                        },
                        onClick = viewModel::selectItem,
                        onLongClick = viewModel::selectItem,
                        border = BorderStroke(0.dp, Color.Transparent)
                    )
                }
            }
        }
    }
}