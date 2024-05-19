package com.niyaj.category.settings

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import com.niyaj.common.tags.CategoryConstants.IMPORT_CATEGORY_BTN_TEXT
import com.niyaj.common.tags.CategoryConstants.IMPORT_CATEGORY_NOTE_TEXT
import com.niyaj.common.tags.CategoryConstants.IMPORT_CATEGORY_OPN_FILE
import com.niyaj.common.tags.CategoryConstants.IMPORT_CATEGORY_TITLE
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.domain.utils.ImportExport
import com.niyaj.model.Category
import com.niyaj.ui.components.EmptyImportScreen
import com.niyaj.ui.components.InfoText
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardScaffoldRouteNew
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrollingUp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@Destination
fun ImportCategoryScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: CategorySettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()

    val importedCategories = viewModel.importedCategories.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()
    var importJob: Job? = null

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

    val importLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            it.data?.data?.let {
                importJob?.cancel()

                importJob = scope.launch {
                    val data = ImportExport.readDataAsync<Category>(context, it)

                    viewModel.onEvent(CategorySettingsEvent.OnImportCategoriesFromFile(data))
                }
            }
        }

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

    TrackScreenViewEvent(screenName = "Category Import Screen")

    BackHandler {
        if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        } else {
            navigator.navigateUp()
        }
    }

    StandardScaffoldRouteNew(
        title = if (selectedItems.isEmpty()) IMPORT_CATEGORY_TITLE else "${selectedItems.size} Selected",
        showBackButton = selectedItems.isEmpty(),
        showBottomBar = importedCategories.isNotEmpty(),
        navActions = {
            AnimatedVisibility(
                visible = importedCategories.isNotEmpty(),
            ) {
                IconButton(
                    onClick = viewModel::selectAllItems,
                ) {
                    Icon(
                        imageVector = PoposIcons.Checklist,
                        contentDescription = Constants.SELECT_ALL_ICON,
                    )
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
                InfoText(text = "${if (selectedItems.isEmpty()) "All" else "${selectedItems.size}"} category will be imported.")

                StandardButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(IMPORT_CATEGORY_BTN_TEXT),
                    enabled = true,
                    text = IMPORT_CATEGORY_BTN_TEXT,
                    icon = PoposIcons.Download,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                    onClick = {
                        scope.launch {
                            viewModel.onEvent(CategorySettingsEvent.ImportCategoriesToDatabase)
                        }
                    },
                )
            }
        },
        fabPosition = FabPosition.End,
        floatingActionButton = {
            ScrollToTop(
                visible = !lazyGridState.isScrollingUp(),
                onClick = {
                    scope.launch {
                        lazyGridState.animateScrollToItem(index = 0)
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
        onBackClick = navigator::navigateUp,
    ) {
        Crossfade(
            targetState = importedCategories.isEmpty(),
            label = "Imported Categories",
        ) { categories ->
            if (categories) {
                EmptyImportScreen(
                    text = IMPORT_CATEGORY_NOTE_TEXT,
                    buttonText = IMPORT_CATEGORY_OPN_FILE,
                    icon = PoposIcons.FileOpen,
                    onClick = {
                        scope.launch {
                            askForPermissions()
                            val result = ImportExport.openFile(context)
                            importLauncher.launch(result)
                        }
                    },
                )
            } else {
                TrackScrollJank(
                    scrollableState = lazyGridState,
                    stateName = "Imported Category::List",
                )

                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    contentPadding = PaddingValues(SpaceSmall),
                    columns = GridCells.Fixed(2),
                    state = lazyGridState,
                ) {
                    items(
                        items = importedCategories,
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
}