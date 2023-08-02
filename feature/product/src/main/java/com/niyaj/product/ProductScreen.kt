package com.niyaj.product

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TurnedInNot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.data.utils.ProductTestTags.CREATE_NEW_PRODUCT
import com.niyaj.data.utils.ProductTestTags.DELETE_PRODUCT_MESSAGE
import com.niyaj.data.utils.ProductTestTags.DELETE_PRODUCT_TITLE
import com.niyaj.data.utils.ProductTestTags.NO_ITEMS_IN_PRODUCT
import com.niyaj.data.utils.ProductTestTags.PRODUCT_NOT_AVAIlABLE
import com.niyaj.data.utils.ProductTestTags.PRODUCT_SCREEN_TITLE
import com.niyaj.data.utils.ProductTestTags.PRODUCT_SEARCH_PLACEHOLDER
import com.niyaj.data.utils.ProductTestTags.PRODUCT_TAG
import com.niyaj.designsystem.theme.SpaceLarge
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Product
import com.niyaj.product.destinations.AddEditProductScreenDestination
import com.niyaj.ui.components.CategoriesData
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.NoteText
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.StandardScaffold
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination(
    route = Screens.ProductsScreen
)
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
        title = if (selectedItems.isEmpty()) PRODUCT_SCREEN_TITLE else "${selectedItems.size} Selected",
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
        navActions = {
             ScaffoldNavActions(
                 placeholderText = PRODUCT_SEARCH_PLACEHOLDER,
                 showSettingsIcon = true,
                 selectionCount = selectedItems.size,
                 showSearchIcon = showSearchBar,
                 searchText = searchText,
                 onEditClick = {
                     navController.navigate(AddEditProductScreenDestination(selectedItems.first()))
                 },
                 onDeleteClick = {
                     openDialog.value = true
                 },
                 onSettingsClick = {

                 },
                 onSelectAllClick = viewModel::selectAllItems,
                 onClearClick = viewModel::clearSearchText,
                 onSearchClick = viewModel::openSearchBar,
                 onSearchTextChanged = viewModel::searchTextChanged
             )
        },
        fabPosition = if (lazyListState.isScrolled) FabPosition.End else FabPosition.Center,
        selectionCount = selectedItems.size,
        showBackButton = showSearchBar,
        onDeselect = viewModel::deselectItems,
        onBackClick = viewModel::closeSearchBar,
        snackbarHostState = snackbarState,
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