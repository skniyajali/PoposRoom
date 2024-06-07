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

package com.niyaj.cart.dineIn

import android.annotation.SuppressLint
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.cart.CartViewModel
import com.niyaj.cart.DineInEvent
import com.niyaj.cart.components.CartScreenContent
import com.niyaj.ui.utils.UiEvent

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun DineInScreen(
    onClickCreateOrder: () -> Unit,
    onClickEditOrder: (Int) -> Unit,
    onClickOrderDetails: (Int) -> Unit,
    onNavigateToOrderScreen: () -> Unit,
    viewModel: CartViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by viewModel.dineInState.collectAsStateWithLifecycle()
    val selectedDineInOrder = viewModel.selectedDineInOrder.toList()
    val addOnItems by viewModel.addOnItems.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(null)

    LaunchedEffect(key1 = event) {
        event?.let { event ->
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

    CartScreenContent(
        modifier = Modifier,
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        selectedItems = selectedDineInOrder,
        addOnItems = addOnItems,
        onClickCreateOrder = onClickCreateOrder,
        onClickEditOrder = onClickEditOrder,
        onClickOrderDetails = onClickOrderDetails,
        onClickSelectAll = {
            viewModel.onEvent(DineInEvent.SelectAllDineInCart)
        },
        onClickPlaceAllOrder = {
            viewModel.onEvent(DineInEvent.PlaceAllDineInCart)
        },
        onEvent = viewModel::onEvent,
    )
}
