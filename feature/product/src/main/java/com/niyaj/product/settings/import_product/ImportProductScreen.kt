package com.niyaj.product.settings.import_product

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
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
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.ProductTestTags
import com.niyaj.common.tags.ProductTestTags.IMPORT_PRODUCTS_NOTE_TEXT
import com.niyaj.common.tags.ProductTestTags.IMPORT_PRODUCTS_OPN_FILE
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.model.Product
import com.niyaj.product.components.ProductCard
import com.niyaj.product.settings.ProductSettingsEvent
import com.niyaj.product.settings.ProductSettingsViewModel
import com.niyaj.ui.components.EmptyImportScreen
import com.niyaj.ui.components.InfoText
import com.niyaj.ui.components.ScrollToTop
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrollingUp
import com.niyaj.utils.ImportExport
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Destination
@Composable
fun ImportProductScreen(
    navController: NavController,
    viewModel: ProductSettingsViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    val importedProducts = viewModel.importedProducts.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()
    val selectionCount = rememberUpdatedState(newValue = selectedItems.size)

    var importJob : Job? = null

    val importLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            it.data?.data?.let {
                importJob?.cancel()

                importJob = scope.launch {
                    val data = ImportExport.readData<Product>(context, it)

                    viewModel.onEvent(ProductSettingsEvent.OnImportProductsFromFile(data))
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

    fun onBackClick() {
        if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        }  else {
            navController.navigateUp()
        }
    }

    BackHandler {
        onBackClick()
    }

    TrackScreenViewEvent(screenName = "Product Import Screen")
    
    StandardScaffoldNew(
        navController = navController,
        title = if (selectedItems.isEmpty()) ProductTestTags.IMPORT_PRODUCTS_TITLE else "${selectedItems.size} Selected",
        navigationIcon = {
            AnimatedContent(
                targetState = selectionCount,
                transitionSpec = {
                    (fadeIn()).togetherWith(
                        fadeOut(animationSpec = tween(200))
                    )
                },
                label = "navigationIcon",
                contentKey = { it }
            ) { intState ->
                if (intState.value != 0) {
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
        showBottomBar = importedProducts.isNotEmpty() && lazyListState.isScrollingUp(),
        navActions = {
            AnimatedVisibility(
                visible = importedProducts.isNotEmpty()
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
                InfoText(text = "${if (selectedItems.isEmpty()) "All" else "${selectedItems.size}"} products will be imported.")

                StandardButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(ProductTestTags.IMPORT_PRODUCTS_BTN_TEXT),
                    enabled = true,
                    text = ProductTestTags.IMPORT_PRODUCTS_BTN_TEXT,
                    icon = Icons.Default.Download,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    onClick = {
                        scope.launch {
                            viewModel.onEvent(ProductSettingsEvent.ImportProductsToDatabase)
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
            targetState = importedProducts.isEmpty(),
            label = "Imported Products"
        ) { productsAvailable ->
            if (productsAvailable) {
                EmptyImportScreen(
                    text = IMPORT_PRODUCTS_NOTE_TEXT,
                    buttonText = IMPORT_PRODUCTS_OPN_FILE,
                    icon = Icons.Default.FileOpen,
                    onClick = {
                        scope.launch {
                            val result = ImportExport.openFile(context)
                            importLauncher.launch(result)
                        }
                    }
                )
            }else {
                TrackScrollJank(scrollableState = lazyListState, stateName = "Imported Products::List")

                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(SpaceSmall)
                ) {
                    itemsIndexed(
                        items = importedProducts,
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
}