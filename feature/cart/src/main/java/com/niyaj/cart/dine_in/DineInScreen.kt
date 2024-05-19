/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.cart.dine_in

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.cart.components.CartFooterPlaceOrder
import com.niyaj.cart.components.CartItems
import com.niyaj.core.ui.R
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.isScrollingUp
import kotlinx.coroutines.flow.collectLatest

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun DineInScreen(
    onClickCreateOrder: () -> Unit,
    onClickEditOrder: (Int) -> Unit,
    onClickOrderDetails: (Int) -> Unit,
    onNavigateToOrderScreen: () -> Unit,
    viewModel: DineInViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val lazyListState = rememberLazyListState()
    val dineInOrders = viewModel.state.collectAsStateWithLifecycle().value.items
    val isLoading = viewModel.state.collectAsStateWithLifecycle().value.isLoading

    val countTotalDineInItems = dineInOrders.size
    val selectedDineInOrder = viewModel.selectedItems.toList()
    val countSelectedDineInItem = selectedDineInOrder.size

    val addOnItems = viewModel.addOnItems.collectAsStateWithLifecycle().value

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.successMessage,
                        actionLabel = "View",
                        duration = SnackbarDuration.Short,
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        onNavigateToOrderScreen()
                    }
                }

                is UiEvent.OnError -> {
                    snackbarHostState.showSnackbar(
                        message = event.errorMessage,
                        duration = SnackbarDuration.Short,
                    )
                }
            }
        }
    }

    TrackScreenViewEvent(screenName = "DineIn Tab::Cart")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = dineInOrders.isNotEmpty() && lazyListState.isScrollingUp(),
                label = "BottomBar",
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { fullHeight ->
                        fullHeight / 4
                    },
                ),
                exit = fadeOut() + slideOutVertically(
                    targetOffsetY = { fullHeight ->
                        fullHeight / 4
                    },
                ),
            ) {
                CartFooterPlaceOrder(
                    countTotalItems = countTotalDineInItems,
                    countSelectedItem = countSelectedDineInItem,
                    showPrintBtn = false,
                    onClickSelectAll = {
                        viewModel.onEvent(DineInEvent.SelectAllDineInOrder)
                    },
                    onClickPlaceAllOrder = {
                        viewModel.onEvent(DineInEvent.PlaceAllDineInOrder)
                    },
                    onClickPrintAllOrder = {},
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
        if (isLoading) {
            LoadingIndicator()
        } else if (countTotalDineInItems == 0) {
            ItemNotAvailable(
                text = "Dine in order not available",
                buttonText = "Add Item To Cart",
                image = painterResource(R.drawable.emptycart),
                onClick = onClickCreateOrder,
            )
        } else {
            TrackScrollJank(scrollableState = lazyListState, stateName = "DineIn Orders::Cart")

            CartItems(
                modifier = Modifier.fillMaxSize(),
                listState = lazyListState,
                cartItems = dineInOrders,
                selectedCartItems = selectedDineInOrder,
                addOnItems = addOnItems,
                onSelectCartOrder = {
                    viewModel.onEvent(DineInEvent.SelectDineInOrder(it))
                },
                onClickEditOrder = onClickEditOrder,
                onClickViewOrder = onClickOrderDetails,
                onClickDecreaseQty = { cartOrderId, productId ->
                    viewModel.onEvent(DineInEvent.DecreaseQuantity(cartOrderId, productId))
                },
                onClickIncreaseQty = { cartOrderId, productId ->
                    viewModel.onEvent(DineInEvent.IncreaseQuantity(cartOrderId, productId))
                },
                onClickAddOnItem = { addOnItemId, cartOrderId ->
                    viewModel.onEvent(DineInEvent.UpdateAddOnItemInCart(addOnItemId, cartOrderId))
                },
                onClickPlaceOrder = {
                    viewModel.onEvent(DineInEvent.PlaceDineInOrder(it))
                },
            )
        }
    }
}