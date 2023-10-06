package com.niyaj.customer.settings

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.niyaj.common.tags.CustomerTestTags.IMPORT_CUSTOMER_BTN_TEXT
import com.niyaj.common.tags.CustomerTestTags.IMPORT_CUSTOMER_NOTE_TEXT
import com.niyaj.common.tags.CustomerTestTags.IMPORT_CUSTOMER_OPN_FILE
import com.niyaj.common.tags.CustomerTestTags.IMPORT_CUSTOMER_TITLE
import com.niyaj.common.utils.Constants
import com.niyaj.customer.CustomerData
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.model.Customer
import com.niyaj.ui.components.ImportScreen
import com.niyaj.ui.components.NoteCard
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
fun CustomerImportScreen(
    navController: NavController,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: CustomerSettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

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
                    val data = ImportExport.readData<Customer>(context, it)

                    viewModel.onEvent(CustomerSettingsEvent.OnImportCustomerItemsFromFile(data))
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
        title = if (selectedItems.isEmpty()) IMPORT_CUSTOMER_TITLE else "${selectedItems.size} Selected",
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
                NoteCard(text = "${if (selectedItems.isEmpty()) "All" else "${selectedItems.size}"} addon item will be imported.")

                StandardButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(IMPORT_CUSTOMER_BTN_TEXT),
                    enabled = true,
                    text = IMPORT_CUSTOMER_BTN_TEXT,
                    icon = Icons.Default.Download,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    onClick = {
                        scope.launch {
                            viewModel.onEvent(CustomerSettingsEvent.ImportCustomerItemsToDatabase)
                        }
                    }
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
                }
            )
        }
    ) {
        Crossfade(
            targetState = importedItems.isEmpty(),
            label = "Imported Items"
        ) { itemAvailable ->
            if (itemAvailable) {
                ImportScreen(
                    text = IMPORT_CUSTOMER_NOTE_TEXT,
                    buttonText = IMPORT_CUSTOMER_OPN_FILE,
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
                LazyColumn(
                    modifier = Modifier
                        .padding(SpaceSmall),
                    state = lazyListState
                ) {
                    items(
                        items = importedItems,
                        key = { it.customerId }
                    ) { item: Customer ->
                        CustomerData(
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