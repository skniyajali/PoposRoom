package com.niyaj.category.settings

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.category.components.CategoryData
import com.niyaj.category.destinations.AddEditCategoryScreenDestination
import com.niyaj.common.tags.CategoryConstants
import com.niyaj.common.tags.CategoryConstants.CATEGORY_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.CategoryConstants.EXPORT_CATEGORY_BTN
import com.niyaj.common.tags.CategoryConstants.EXPORT_CATEGORY_BTN_TEXT
import com.niyaj.common.tags.CategoryConstants.EXPORT_CATEGORY_FILE_NAME
import com.niyaj.common.tags.CategoryConstants.EXPORT_CATEGORY_TITLE
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.domain.utils.ImportExport
import com.niyaj.model.Category
import com.niyaj.ui.components.InfoText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardScaffoldRouteNew
import com.niyaj.ui.components.StandardSearchBar
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Destination
@Composable
fun ExportCategoryScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: CategorySettingsViewModel = hiltViewModel(),
) {
    TrackScreenViewEvent(screenName = "Category Export Screen")

    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyGridState()

    val categories = viewModel.categories.collectAsStateWithLifecycle().value
    val exportedCategories = viewModel.exportedCategories.collectAsStateWithLifecycle().value

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    val selectedItems = viewModel.selectedItems.toList()

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

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

    val context = LocalContext.current

    val hasStoragePermission = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ),
    )

    val askForPermissions = {
        if (!hasStoragePermission.allPermissionsGranted) {
            hasStoragePermission.launchMultiplePermissionRequest()
        }
    }

    val exportLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            it.data?.data?.let {
                scope.launch {
                    val result = ImportExport.writeDataAsync(context, it, exportedCategories)

                    if (result.isSuccess) {
                        resultBackNavigator.navigateBack("${exportedCategories.size} Categories has been exported.")
                    } else {
                        resultBackNavigator.navigateBack("Unable to export categories.")
                    }
                }
            }
        }

    fun onBackClick() {
        if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        } else if (showSearchBar) {
            viewModel.closeSearchBar()
        } else {
            navigator.navigateUp()
        }
    }

    BackHandler {
        onBackClick()
    }

    StandardScaffoldRouteNew(
        title = if (selectedItems.isEmpty()) EXPORT_CATEGORY_TITLE else "${selectedItems.size} Selected",
        showBackButton = selectedItems.isEmpty() || showSearchBar,
        showBottomBar = categories.isNotEmpty(),
        navActions = {
            if (showSearchBar) {
                StandardSearchBar(
                    searchText = searchText,
                    placeholderText = CATEGORY_SEARCH_PLACEHOLDER,
                    onClearClick = viewModel::clearSearchText,
                    onSearchTextChanged = viewModel::searchTextChanged,
                )
            } else {
                if (categories.isNotEmpty()) {
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
                    .padding(SpaceSmallMax),
                verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            ) {
                InfoText(text = "${if (selectedItems.isEmpty()) "All" else "${selectedItems.size}"} categories will be exported.")

                StandardButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(EXPORT_CATEGORY_BTN),
                    enabled = true,
                    text = EXPORT_CATEGORY_BTN_TEXT,
                    icon = PoposIcons.Upload,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                    ),
                    onClick = {
                        scope.launch {
                            askForPermissions()
                            val result = ImportExport.createFile(
                                context = context,
                                fileName = EXPORT_CATEGORY_FILE_NAME,
                            )
                            exportLauncher.launch(result)
                            viewModel.onEvent(CategorySettingsEvent.GetExportedCategory)
                        }
                    },
                )
            }
        },
        fabPosition = FabPosition.End,
        floatingActionButton = {
            ScrollToTop(
                visible = !lazyListState.isScrollingUp(),
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                },
            )
        },
        navigationIcon = {
            IconButton(
                onClick = viewModel::deselectItems,
            ) {
                Icon(
                    imageVector = PoposIcons.Close,
                    contentDescription = "Deselect All",
                )
            }
        },
        onBackClick = { onBackClick() },
    ) {
        if (categories.isEmpty()) {
            ItemNotAvailable(
                text = if (searchText.isEmpty()) CategoryConstants.CATEGORY_NOT_AVAILABLE else Constants.SEARCH_ITEM_NOT_FOUND,
                buttonText = CategoryConstants.CREATE_NEW_CATEGORY,
                onClick = {
                    navigator.navigate(AddEditCategoryScreenDestination())
                },
            )
        } else {
            TrackScrollJank(scrollableState = lazyListState, stateName = "Exported Category::List")

            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SpaceSmall),
                columns = GridCells.Fixed(2),
                state = lazyListState,
            ) {
                items(
                    items = categories,
                    key = { it.categoryId },
                ) { item: Category ->
                    CategoryData(
                        item = item,
                        doesSelected = {
                            selectedItems.contains(it)
                        },
                        onClick = {
                            viewModel.selectItem(it)
                        },
                        onLongClick = viewModel::selectItem,
                    )
                }
            }
        }
    }
}