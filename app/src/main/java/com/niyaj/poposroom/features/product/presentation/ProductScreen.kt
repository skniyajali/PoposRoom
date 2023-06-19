package com.niyaj.poposroom.features.product.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TurnedInNot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.poposroom.features.category.domain.model.Category
import com.niyaj.poposroom.features.category.domain.utils.CategoryConstants
import com.niyaj.poposroom.features.common.components.CircularBox
import com.niyaj.poposroom.features.common.components.ItemNotAvailable
import com.niyaj.poposroom.features.common.components.LoadingIndicator
import com.niyaj.poposroom.features.common.components.NoteText
import com.niyaj.poposroom.features.common.components.StandardFAB
import com.niyaj.poposroom.features.common.components.StandardScaffold
import com.niyaj.poposroom.features.common.event.UiState
import com.niyaj.poposroom.features.common.ui.theme.SpaceLarge
import com.niyaj.poposroom.features.common.ui.theme.SpaceMini
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmallMax
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.common.utils.isScrolled
import com.niyaj.poposroom.features.common.utils.toPrettyDate
import com.niyaj.poposroom.features.common.utils.toRupee
import com.niyaj.poposroom.features.destinations.AddEditProductScreenDestination
import com.niyaj.poposroom.features.product.domain.model.Product
import com.niyaj.poposroom.features.product.domain.utils.ProductTestTags.CREATE_NEW_PRODUCT
import com.niyaj.poposroom.features.product.domain.utils.ProductTestTags.DELETE_PRODUCT_MESSAGE
import com.niyaj.poposroom.features.product.domain.utils.ProductTestTags.DELETE_PRODUCT_TITLE
import com.niyaj.poposroom.features.product.domain.utils.ProductTestTags.NO_ITEMS_IN_PRODUCT
import com.niyaj.poposroom.features.product.domain.utils.ProductTestTags.PRODUCT_NOT_AVAIlABLE
import com.niyaj.poposroom.features.product.domain.utils.ProductTestTags.PRODUCT_SCREEN_TITLE
import com.niyaj.poposroom.features.product.domain.utils.ProductTestTags.PRODUCT_SEARCH_PLACEHOLDER
import com.niyaj.poposroom.features.product.domain.utils.ProductTestTags.PRODUCT_TAG
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@Destination
@Composable
fun ProductScreen(
    navController: NavController,
    viewModel: ProductViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditProductScreenDestination, String>
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    val state = viewModel.products.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()

    val lazyListState = rememberLazyListState()

    val selectedCategory = viewModel.selectedCategory.collectAsStateWithLifecycle().value

    val showFab = viewModel.totalItems.isNotEmpty() && selectedCategory == 0

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    val categories = viewModel.categories.collectAsStateWithLifecycle().value

    val openDialog = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
                is UiEvent.IsLoading -> {}
                is UiEvent.OnError -> {
                    scope.launch {
                        snackbarState.showSnackbar(data.errorMessage)
                    }
                    viewModel.deselectItems()
                }

                is UiEvent.OnSuccess -> {
                    scope.launch {
                        snackbarState.showSnackbar(data.successMessage)
                    }
                    viewModel.deselectItems()
                }
            }
        }
    }

    resultRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {
                viewModel.deselectItems()
            }
            is NavResult.Value -> {
                scope.launch {
                    viewModel.deselectItems()
                    snackbarState.showSnackbar(result.value)
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
        if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        } else if (showSearchBar) {
            viewModel.closeSearchBar()
        }
    }

    StandardScaffold(
        navController = navController,
        snackbarHostState = snackbarState,
        title = if (selectedItems.isEmpty()) PRODUCT_SCREEN_TITLE else "${selectedItems.size} Selected",
        placeholderText = PRODUCT_SEARCH_PLACEHOLDER,
        showSearchBar = showSearchBar,
        showSettings = false,
        selectionCount = selectedItems.size,
        searchText = searchText,
        showBackButton = showSearchBar,
        floatingActionButton = {
            StandardFAB(
                showScrollToTop = lazyListState.isScrolled,
                fabText = CREATE_NEW_PRODUCT,
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navController.navigate(AddEditProductScreenDestination())
                },
                onClickScroll = {
                    scope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                }
            )
        },
        fabPosition = if (lazyListState.isScrolled) FabPosition.End else FabPosition.Center,
        onEditClick = {
            navController.navigate(AddEditProductScreenDestination(selectedItems.first()))
        },
        onDeleteClick = {
            openDialog.value = true
        },
        onDeselect = viewModel::deselectItems,
        onSelectAllClick = viewModel::selectAllItems,
        onSearchTextChanged = viewModel::searchTextChanged,
        onSearchClick = viewModel::openSearchBar,
        onBackClick = viewModel::closeSearchBar,
        onClearClick = viewModel::clearSearchText
    ) { _ ->
        Column(
            modifier = Modifier
                .padding(SpaceSmall),
        ) {
            when(state) {
                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) PRODUCT_NOT_AVAIlABLE else NO_ITEMS_IN_PRODUCT,
                        buttonText = CREATE_NEW_PRODUCT,
                        onClick = {
                            navController.navigate(AddEditProductScreenDestination())
                        }
                    )
                }
                is UiState.Loading -> LoadingIndicator()
                is UiState.Success -> {
                    CategoriesData(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onSelect = viewModel::selectCategory
                    )

                    LazyColumn(
                        state = lazyListState,
                    ) {
                        itemsIndexed(state.data) { index, item ->
                            ProductData(
                                item = item,
                                doesSelected = {
                                    selectedItems.contains(it)
                                },
                                onClick = {
                                    if (selectedItems.isNotEmpty()) {
                                        viewModel.selectItem(it)
                                    }
                                },
                                onLongClick = viewModel::selectItem
                            )

                            if (index == state.data.size - 1) {
                                Spacer(modifier = Modifier.height(SpaceLarge))
                                Spacer(modifier = Modifier.height(SpaceLarge))
                            }
                        }
                    }
                }
            }
        }
    }


    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
                viewModel.deselectItems()
            },
            title = {
                Text(text = DELETE_PRODUCT_TITLE)
            },
            text = {
                Text(
                    text = DELETE_PRODUCT_MESSAGE
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


@Composable
fun CategoriesData(
    categories: List<Category>,
    selectedCategory: Int,
    onSelect: (Int) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { category ->
            CategoryData(
                item = category,
                doesSelected = {
                    selectedCategory == it
                } ,
                onClick = onSelect,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryData(
    modifier: Modifier = Modifier,
    item: Category,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    selectedColor: Color = MaterialTheme.colorScheme.secondary,
    unselectedColor: Color = MaterialTheme.colorScheme.surface
) {
    val color = if (doesSelected(item.categoryId)) selectedColor else unselectedColor

    ElevatedCard(
        modifier = modifier
            .testTag(CategoryConstants.CATEGORY_ITEM_TAG.plus(item.categoryId))
            .padding(SpaceSmall),
        onClick = {
            onClick(item.categoryId)
        },
        colors = CardDefaults.elevatedCardColors(
            containerColor = color
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularBox(
                icon = Icons.Default.Category,
                doesSelected = doesSelected(item.categoryId),
                size = 25.dp,
                text = item.categoryName
            )

            Spacer(modifier = Modifier.width(SpaceSmallMax))

            Text(text = item.categoryName)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductData(
    modifier: Modifier = Modifier,
    item: Product,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
) {
    val borderStroke = if (doesSelected(item.productId)) border else null

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
            .then(borderStroke?.let {
                Modifier.border(it, RoundedCornerShape(SpaceMini))
            } ?: Modifier)
            .clip(RoundedCornerShape(SpaceMini))
            .combinedClickable(
                onClick = {
                    onClick(item.productId)
                },
                onLongClick = {
                    onLongClick(item.productId)
                },
            ),
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ListItem(
                modifier = modifier
                    .testTag(PRODUCT_TAG.plus(item.productId))
                    .fillMaxWidth(),
                headlineContent = {
                    Text(
                        text = item.productName,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                supportingContent = {
                    Text(text = item.productPrice.toRupee)
                },
                leadingContent = {
                    CircularBox(
                        icon = Icons.Default.Person,
                        doesSelected = doesSelected(item.productId),
                        text = item.productName,
                        showBorder = !item.productAvailability,
                    )
                },
                trailingContent = {
                    NoteText(
                        text = item.createdAt.toPrettyDate(),
                        icon = Icons.Default.CalendarMonth
                    )
                }
            )

            if (item.productDescription.isNotEmpty()) {
                NoteText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall),
                    text = item.productDescription,
                    icon = Icons.Default.TurnedInNot,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}