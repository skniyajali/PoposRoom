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

package com.niyaj.settings.dataDeletion

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.PaymentScreenTags
import com.niyaj.common.tags.ProductTestTags.ADD_EDIT_PRODUCT_BUTTON
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.StandardCheckboxWithText
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.flow.collectLatest

@Destination(route = Screens.DATA_DELETION_SETTINGS_SCREEN)
@Composable
fun DataDeletionSettingsScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    modifier: Modifier = Modifier,
    viewModel: DataDeletionSettingsViewModel = hiltViewModel(),
) {
    val reportError by viewModel.reportIntervalError.collectAsStateWithLifecycle()
    val orderError by viewModel.orderIntervalError.collectAsStateWithLifecycle()
    val cartError by viewModel.cartIntervalError.collectAsStateWithLifecycle()
    val expenseError by viewModel.expenseIntervalError.collectAsStateWithLifecycle()
    val marketListError by viewModel.marketListIntervalError.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.eventFlow.collectLatest {
            when (it) {
                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(it.errorMessage)
                }

                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(it.successMessage)
                }
            }
        }
    }

    DataDeletionSettingsScreenContent(
        modifier = modifier,
        state = viewModel.state,
        reportError = reportError,
        orderError = orderError,
        cartError = cartError,
        expenseError = expenseError,
        marketListError = marketListError,
        onEvent = viewModel::onEvent,
        onBackClick = navigator::navigateUp,
    )
}

@VisibleForTesting
@Composable
internal fun DataDeletionSettingsScreenContent(
    state: DataDeletionSettingsState,
    reportError: String?,
    orderError: String?,
    cartError: String?,
    expenseError: String?,
    marketListError: String?,
    onEvent: (DataDeletionEvent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Data Deletion Settings",
    lazyListState: LazyListState = rememberLazyListState(),
) {
    TrackScrollJank(scrollableState = lazyListState, stateName = "Data Deletion Settings::Fields")

    val enableBtn =
        listOf(reportError, orderError, cartError, expenseError, marketListError).all { it == null }

    PoposSecondaryScaffold(
        title = title,
        onBackClick = onBackClick,
        modifier = modifier,
        showBackButton = true,
        showBottomBar = true,
        bottomBar = {
            PoposButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_PRODUCT_BUTTON),
                enabled = enableBtn,
                text = "Update Settings",
                icon = PoposIcons.Edit,
                onClick = {
                    onEvent(DataDeletionEvent.OnSave)
                },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .testTag(PaymentScreenTags.ADD_EDIT_PAYMENT_SCREEN)
                .fillMaxWidth()
                .padding(paddingValues),
            contentPadding = PaddingValues(SpaceMedium),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            item(key = "report") {
                StandardOutlinedTextField(
                    label = "Report Interval",
                    leadingIcon = PoposIcons.Receipt,
                    value = state.reportInterval,
                    onValueChange = { onEvent(DataDeletionEvent.OnReportIntervalChange(it)) },
                    isError = reportError != null,
                    errorText = reportError,
                    keyboardType = KeyboardType.Number,
                    message = "Report will be deleted before these days",
                )
            }

            item(key = "order") {
                StandardOutlinedTextField(
                    label = "Order Interval",
                    leadingIcon = PoposIcons.Order,
                    value = state.orderInterval,
                    onValueChange = { onEvent(DataDeletionEvent.OnOrderIntervalChange(it)) },
                    isError = orderError != null,
                    errorText = orderError,
                    keyboardType = KeyboardType.Number,
                    message = "Orders will be deleted before these days",
                )
            }

            item(key = "cart") {
                StandardOutlinedTextField(
                    label = "Cart Interval",
                    leadingIcon = PoposIcons.Cart,
                    value = state.cartInterval,
                    onValueChange = { onEvent(DataDeletionEvent.OnCartIntervalChange(it)) },
                    isError = cartError != null,
                    errorText = cartError,
                    keyboardType = KeyboardType.Number,
                    message = "Cart data will be deleted before these days",
                )
            }

            item(key = "expense") {
                StandardOutlinedTextField(
                    label = "Expense Interval",
                    leadingIcon = PoposIcons.TrendingUp,
                    value = state.expenseInterval,
                    onValueChange = { onEvent(DataDeletionEvent.OnExpenseIntervalChange(it)) },
                    isError = expenseError != null,
                    errorText = expenseError,
                    keyboardType = KeyboardType.Number,
                    message = "Expenses will be deleted before these days",
                )
            }

            item(key = "marketList") {
                StandardOutlinedTextField(
                    label = "Market List Interval",
                    leadingIcon = PoposIcons.ShoppingBag,
                    value = state.marketListInterval,
                    onValueChange = { onEvent(DataDeletionEvent.OnMarketListIntervalChange(it)) },
                    isError = marketListError != null,
                    errorText = marketListError,
                    keyboardType = KeyboardType.Number,
                    message = "Market list will be deleted before these days",
                )
            }

            item("deleteData") {
                StandardCheckboxWithText(
                    text = "Delete data before interval",
                    checked = state.deleteDataBeforeInterval,
                    onCheckedChange = {
                        onEvent(DataDeletionEvent.OnDeleteDataBeforeIntervalChange)
                    },
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun DataDeletionSettingsScreenContentPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        DataDeletionSettingsScreenContent(
            modifier = modifier,
            state = DataDeletionSettingsState(),
            reportError = null,
            orderError = null,
            cartError = null,
            expenseError = null,
            marketListError = null,
            onEvent = {},
            onBackClick = {},
        )
    }
}
