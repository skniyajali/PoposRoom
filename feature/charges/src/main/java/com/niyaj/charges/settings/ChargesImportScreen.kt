package com.niyaj.charges.settings

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileOpen
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
import com.niyaj.charges.ChargesData
import com.niyaj.common.tags.ChargesTestTags.IMPORT_CHARGES_BTN_TEXT
import com.niyaj.common.tags.ChargesTestTags.IMPORT_CHARGES_NOTE_TEXT
import com.niyaj.common.tags.ChargesTestTags.IMPORT_CHARGES_OPN_FILE
import com.niyaj.common.tags.ChargesTestTags.IMPORT_CHARGES_TITLE
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.model.Charges
import com.niyaj.ui.components.EmptyImportScreen
import com.niyaj.ui.components.InfoText
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrollingUp
import com.niyaj.utils.ImportExport
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Destination
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ChargesImportScreen(
    navController: NavController,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: ChargesSettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()

    val importedItems = viewModel.importedItems.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()
    var importJob : Job? = null

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

    val importLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            it.data?.data?.let {
                importJob?.cancel()

                importJob = scope.launch {
                    val data = ImportExport.readData<Charges>(context, it)

                    viewModel.onEvent(ChargesSettingsEvent.OnImportChargesItemsFromFile(data))
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

    StandardScaffoldNew(
        navController = navController,
        title = if (selectedItems.isEmpty()) IMPORT_CHARGES_TITLE else "${selectedItems.size} Selected",
        showBackButton = true,
        showBottomBar = importedItems.isNotEmpty(),
        navActions = {
            AnimatedVisibility(
                visible = importedItems.isNotEmpty()
            ) {
                IconButton(
                    onClick = viewModel::selectAllItems
                ) {
                    Icon(
                        imageVector = Icons.Default.Checklist,
                        contentDescription = Constants.SELECTALL_ICON
                    )
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
                InfoText(text = "${if (selectedItems.isEmpty()) "All" else "${selectedItems.size}"} charges item will be imported.")

                StandardButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(IMPORT_CHARGES_BTN_TEXT),
                    enabled = true,
                    text = IMPORT_CHARGES_BTN_TEXT,
                    icon = Icons.Default.Download,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    onClick = {
                        scope.launch {
                            viewModel.onEvent(ChargesSettingsEvent.ImportChargesItemsToDatabase)
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
        Crossfade(
            targetState = importedItems.isEmpty(),
            label = "Imported Items"
        ) { itemAvailable ->
            if (itemAvailable) {
                EmptyImportScreen(
                    text = IMPORT_CHARGES_NOTE_TEXT,
                    buttonText = IMPORT_CHARGES_OPN_FILE,
                    icon = Icons.Default.FileOpen,
                    onClick = {
                        scope.launch {
                            askForPermissions()
                            val result = ImportExport.openFile(context)
                            importLauncher.launch(result)
                        }
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
                        items = importedItems,
                        key = { it.chargesId }
                    ) { item: Charges ->
                        ChargesData(
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
}