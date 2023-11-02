package com.niyaj.daily_market.measure_unit.settings

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upload
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
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.common.tags.MeasureUnitTestTags.CREATE_NEW_UNIT
import com.niyaj.common.tags.MeasureUnitTestTags.EXPORT_UNIT_BTN
import com.niyaj.common.tags.MeasureUnitTestTags.EXPORT_UNIT_BTN_TEXT
import com.niyaj.common.tags.MeasureUnitTestTags.EXPORT_UNIT_FILE_NAME
import com.niyaj.common.tags.MeasureUnitTestTags.EXPORT_UNIT_TITLE
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_NOT_AVAILABLE
import com.niyaj.common.utils.Constants
import com.niyaj.daily_market.destinations.AddEditMarketItemScreenDestination
import com.niyaj.daily_market.measure_unit.MeasureUnitItem
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.model.MeasureUnit
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.NoteCard
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.components.StandardSearchBar
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrollingUp
import com.niyaj.utils.ImportExport
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Destination
@Composable
fun ExportMeasureUnitScreen(
    navController: NavController,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: MeasureUnitSettingsViewModel = hiltViewModel(),
) {

    val scope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()

    val items = viewModel.items.collectAsStateWithLifecycle().value
    val exportedItems = viewModel.exportedItems.collectAsStateWithLifecycle().value

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
        )
    )

    val askForPermissions = {
        if (!hasStoragePermission.allPermissionsGranted) {
            hasStoragePermission.launchMultiplePermissionRequest()
        }
    }

    val exportLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            it.data?.data?.let {
                scope.launch {
                    val result = ImportExport.writeData(context, it, exportedItems)

                    if (result) {
                        resultBackNavigator.navigateBack("${exportedItems.size} Items has been exported.")
                    } else {
                        resultBackNavigator.navigateBack("Unable to export items.")
                    }
                }
            }
        }

    fun onBackClick() {
        if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        } else if (showSearchBar) {
            viewModel.closeSearchBar()
        }else {
            navController.navigateUp()
        }
    }

    BackHandler {
        onBackClick()
    }

    StandardScaffoldNew(
        navController = navController,
        title = if (selectedItems.isEmpty()) EXPORT_UNIT_TITLE else "${selectedItems.size} Selected",
        showBackButton = true,
        showBottomBar = true,
        navActions = {
            if (showSearchBar) {
                StandardSearchBar(
                    searchText = searchText,
                    placeholderText = "Search for categories...",
                    onClearClick = viewModel::clearSearchText,
                    onSearchTextChanged = viewModel::searchTextChanged
                )
            }else {
                if (items.isNotEmpty()) {
                    IconButton(
                        onClick = viewModel::selectAllItems
                    ) {
                        Icon(
                            imageVector = Icons.Default.Checklist,
                            contentDescription = Constants.SELECTALL_ICON
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
                NoteCard(text = "${if (selectedItems.isEmpty()) "All" else "${selectedItems.size}"} items will be exported.")

                StandardButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(EXPORT_UNIT_BTN),
                    enabled = true,
                    text = EXPORT_UNIT_BTN_TEXT,
                    icon = Icons.Default.Upload,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    onClick = {
                        scope.launch {
                            askForPermissions()
                            val result = ImportExport.createFile(context = context, fileName = EXPORT_UNIT_FILE_NAME)
                            exportLauncher.launch(result)
                            viewModel.onEvent(MeasureUnitSettingsEvent.GetExportedItems)
                        }
                    }
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
                }
            )
        }
    ) {
        if(items.isEmpty()) {
            ItemNotAvailable(
                text = if (searchText.isEmpty()) UNIT_NOT_AVAILABLE else Constants.SEARCH_ITEM_NOT_FOUND,
                buttonText = CREATE_NEW_UNIT,
                onClick = {
                    navController.navigate(AddEditMarketItemScreenDestination())
                }
            )
        }else {
            LazyVerticalGrid(
                modifier = Modifier
                    .padding(SpaceSmall),
                columns = GridCells.Fixed(2),
                state = lazyGridState,
            ) {
                items(
                    items = items,
                    key = { it.unitId }
                ) { item: MeasureUnit ->
                    MeasureUnitItem(
                        item = item,
                        doesSelected = {
                            selectedItems.contains(it)
                        },
                        onClick = viewModel::selectItem,
                        onLongClick = viewModel::selectItem
                    )
                }
            }
        }
    }
}