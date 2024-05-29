/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.product.details

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.product.components.ProductDetails
import com.niyaj.product.components.ProductOrderDetails
import com.niyaj.product.components.ProductTotalOrdersDetails
import com.niyaj.product.components.ShareableProductOrderDetails
import com.niyaj.product.destinations.AddEditProductScreenDestination
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.isScrolled
import com.niyaj.ui.utils.isScrollingUp
import com.niyaj.ui.utils.rememberCaptureController
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class)
@Destination
@Composable
fun ProductDetailsScreen(
    productId: Int = 0,
    navigator: DestinationsNavigator,
    onClickOrder: (Int) -> Unit,
    viewModel: ProductDetailsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val captureController = rememberCaptureController()

    val productState = viewModel.product.collectAsStateWithLifecycle().value

    val orderDetailsState = viewModel.orderDetails.collectAsStateWithLifecycle().value

    val totalOrderDetails = viewModel.totalOrders.collectAsStateWithLifecycle().value

    val productPrice = viewModel.productPrice.collectAsStateWithLifecycle().value

    val showShareDialog = viewModel.showDialog.collectAsStateWithLifecycle().value

    var productDetailsExpanded by rememberSaveable { mutableStateOf(false) }

    val pagerState = rememberPagerState { 2 }

    val bluetoothPermissions =
        // Checks if the device has Android 12 or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                ),
            )
        } else {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                ),
            )
        }

    val enableBluetoothContract = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) {}

    // This intent will open the enable bluetooth dialog
    val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

    val bluetoothManager = remember {
        context.getSystemService(BluetoothManager::class.java)
    }

    val bluetoothAdapter: BluetoothAdapter? = remember {
        bluetoothManager.adapter
    }

    val printOrder: () -> Unit = {
        if (bluetoothPermissions.allPermissionsGranted) {
            if (bluetoothAdapter?.isEnabled == true) {
                // Bluetooth is on print the receipt
                viewModel.printProductDetails()
            } else {
                // Bluetooth is off, ask user to turn it on
                enableBluetoothContract.launch(enableBluetoothIntent)
                viewModel.printProductDetails()
            }
        } else {
            bluetoothPermissions.launchMultiplePermissionRequest()
        }
    }

    TrackScreenViewEvent(screenName = "Product Details Screen::$productId")

    PoposSecondaryScaffold(
        title = "Product Details",
        showBackButton = true,
        showFab = lazyListState.isScrollingUp(),
        fabPosition = FabPosition.End,
        floatingActionButton = {
            StandardFAB(
                fabVisible = lazyListState.isScrollingUp(),
                onFabClick = viewModel::onShowDialog,
                onClickScroll = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                },
                showScrollToTop = lazyListState.isScrolled,
                fabIcon = PoposIcons.Share,
            )
        },
        navActions = {
            IconButton(
                onClick = printOrder,
            ) {
                Icon(imageVector = PoposIcons.Print, contentDescription = "Print Details")
            }

            IconButton(
                onClick = viewModel::onShowDialog,
            ) {
                Icon(imageVector = PoposIcons.Share, contentDescription = "Share Details")
            }
        },
        onBackClick = navigator::navigateUp,
    ) {
        TrackScrollJank(scrollableState = lazyListState, stateName = "Product Details::List")

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentPadding = PaddingValues(SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        ) {
            item("TotalOrderDetails") {
                ProductTotalOrdersDetails(details = totalOrderDetails)
            }

            item("ProductDetails") {
                ProductDetails(
                    productState = productState,
                    onExpanded = {
                        productDetailsExpanded = !productDetailsExpanded
                    },
                    doesExpanded = productDetailsExpanded,
                    onClickEdit = {
                        navigator.navigate(AddEditProductScreenDestination(productId))
                    },
                )
            }

            item("OrderDetails") {
                ProductOrderDetails(
                    orderState = orderDetailsState,
                    pagerState = pagerState,
                    productPrice = productPrice,
                    onClickOrder = onClickOrder,
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }

    AnimatedVisibility(
        visible = showShareDialog,
    ) {
        ShareableProductOrderDetails(
            captureController = captureController,
            productState = productState,
            totalOrderDetails = totalOrderDetails,
            ordersState = orderDetailsState,
            onDismiss = viewModel::onDismissDialog,
            onClickShare = {
                captureController.captureLongScreenshot()
            },
            onCaptured = { bitmap, error ->
                bitmap?.let {
                    scope.launch {
                        val uri = viewModel.saveImage(it, context)
                        uri?.let {
                            viewModel.shareContent(context, "Share Image", uri)
                        }
                    }
                }
                error?.let {
                    Log.d("Capturable", "Error: ${it.message}\n${it.stackTrace.joinToString()}")
                }
            },
            onClickPrintOrder = {
                viewModel.onDismissDialog()
                printOrder()
            },
        )
    }
}
