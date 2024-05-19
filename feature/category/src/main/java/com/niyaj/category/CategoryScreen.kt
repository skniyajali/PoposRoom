package com.niyaj.category

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.category.components.CategoryData
import com.niyaj.category.destinations.AddEditCategoryScreenDestination
import com.niyaj.category.destinations.CategorySettingsScreenDestination
import com.niyaj.category.destinations.ExportCategoryScreenDestination
import com.niyaj.category.destinations.ImportCategoryScreenDestination
import com.niyaj.common.tags.CategoryConstants.CATEGORY_NOT_AVAILABLE
import com.niyaj.common.tags.CategoryConstants.CATEGORY_SCREEN_TITLE
import com.niyaj.common.tags.CategoryConstants.CATEGORY_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.CategoryConstants.CREATE_NEW_CATEGORY
import com.niyaj.common.tags.CategoryConstants.DELETE_CATEGORY_ITEM_MESSAGE
import com.niyaj.common.tags.CategoryConstants.DELETE_CATEGORY_ITEM_TITLE
import com.niyaj.common.utils.Constants.SEARCH_ITEM_NOT_FOUND
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Category
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardDialog
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.StandardScaffoldRoute
import com.niyaj.ui.event.UiState
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination(route = Screens.CATEGORY_SCREEN)
@Composable
fun CategoryScreen(
    navigator: DestinationsNavigator,
    viewModel: CategoryViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditCategoryScreenDestination, String>,
    exportRecipient: ResultRecipient<ExportCategoryScreenDestination, String>,
    importRecipient: ResultRecipient<ImportCategoryScreenDestination, String>,
) {
    val scope = rememberCoroutineScope()
    val snackbarState = remember { SnackbarHostState() }
    val state = viewModel.addOnItems.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()

    val lazyGridState = rememberLazyGridState()

    val showFab = viewModel.totalItems.isNotEmpty()

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    val openDialog = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
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

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        } else if (showSearchBar) {
            viewModel.closeSearchBar()
        } else {
            navigator.navigateUp()
        }
    }

    resultRecipient.onNavResult { result ->
        when (result) {
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

    exportRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }

    importRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    snackbarState.showSnackbar(result.value)
                }
            }
        }
    }

    TrackScreenViewEvent(screenName = Screens.CATEGORY_SCREEN)

    StandardScaffoldRoute(
        currentRoute = Screens.CATEGORY_SCREEN,
        title = if (selectedItems.isEmpty()) CATEGORY_SCREEN_TITLE else "${selectedItems.size} Selected",
        floatingActionButton = {
            StandardFAB(
                showScrollToTop = lazyGridState.isScrolled,
                fabText = CREATE_NEW_CATEGORY,
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navigator.navigate(AddEditCategoryScreenDestination())
                },
                onClickScroll = {
                    scope.launch {
                        lazyGridState.animateScrollToItem(0)
                    }
                },
            )
        },
        navActions = {
            ScaffoldNavActions(
                placeholderText = CATEGORY_SEARCH_PLACEHOLDER,
                showSettingsIcon = true,
                selectionCount = selectedItems.size,
                showSearchBar = showSearchBar,
                showSearchIcon = showFab,
                searchText = searchText,
                onEditClick = {
                    navigator.navigate(AddEditCategoryScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {
                    openDialog.value = true
                },
                onSettingsClick = {
                    navigator.navigate(CategorySettingsScreenDestination)
                },
                onSelectAllClick = viewModel::selectAllItems,
                onClearClick = viewModel::clearSearchText,
                onSearchClick = viewModel::openSearchBar,
                onSearchTextChanged = viewModel::searchTextChanged,
            )
        },
        fabPosition = if (lazyGridState.isScrolled) FabPosition.End else FabPosition.Center,
        selectionCount = selectedItems.size,
        showBackButton = showSearchBar,
        onDeselect = viewModel::deselectItems,
        onBackClick = viewModel::closeSearchBar,
        snackbarHostState = snackbarState,
        onNavigateToScreen = navigator::navigate,
    ) { _ ->
        when (state) {
            is UiState.Loading -> LoadingIndicator()

            is UiState.Empty -> {
                ItemNotAvailable(
                    text = if (searchText.isEmpty()) CATEGORY_NOT_AVAILABLE else SEARCH_ITEM_NOT_FOUND,
                    buttonText = CREATE_NEW_CATEGORY,
                    onClick = {
                        navigator.navigate(AddEditCategoryScreenDestination())
                    },
                )
            }

            is UiState.Success -> {
                TrackScrollJank(scrollableState = lazyGridState, stateName = "Category::List")

                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(SpaceSmall),
                    columns = GridCells.Fixed(2),
                    state = lazyGridState,
                ) {
                    items(
                        items = state.data,
                        key = { it.categoryId },
                    ) { item: Category ->
                        CategoryData(
                            item = item,
                            doesSelected = {
                                selectedItems.contains(it)
                            },
                            onClick = {
                                if (selectedItems.isNotEmpty()) {
                                    viewModel.selectItem(it)
                                }
                            },
                            onLongClick = viewModel::selectItem,
                        )
                    }
                }
            }
        }
    }

    if (openDialog.value) {
        StandardDialog(
            title = DELETE_CATEGORY_ITEM_TITLE,
            message = DELETE_CATEGORY_ITEM_MESSAGE,
            onConfirm = {
                openDialog.value = false
                viewModel.deleteItems()
            },
            onDismiss = {
                openDialog.value = false
                viewModel.deselectItems()
            },
        )
    }
}