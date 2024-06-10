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

package com.niyaj.customer.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_TAG
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.model.Customer
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.parameterProvider.CustomerPreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.TrackScrollJank

@Composable
internal fun CustomersData(
    modifier: Modifier = Modifier,
    customers: List<Customer>,
    isInSelectionMode: Boolean,
    doesSelected: (Int) -> Boolean,
    onClickSelectItem: (Int) -> Unit,
    onNavigateToDetails: (Int) -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    TrackScrollJank(scrollableState = lazyListState, stateName = "")

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(SpaceMedium),
        verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        state = lazyListState,
    ) {
        items(
            items = customers,
            key = { it.customerId },
        ) { item: Customer ->
            CustomerData(
                item = item,
                doesSelected = doesSelected,
                onClick = {
                    if (isInSelectionMode) {
                        onClickSelectItem(it)
                    } else {
                        onNavigateToDetails(it)
                    }
                },
                onLongClick = onClickSelectItem,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CustomerData(
    modifier: Modifier = Modifier,
    item: Customer,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
    containerColor: Color = MaterialTheme.colorScheme.background,
) = trace("CustomerData") {
    val borderStroke = if (doesSelected(item.customerId)) border else null

    ListItem(
        modifier = modifier
            .testTag(CUSTOMER_TAG.plus(item.customerId))
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(SpaceMini))
            .then(
                borderStroke?.let {
                    Modifier.border(it, RoundedCornerShape(SpaceMini))
                } ?: Modifier,
            )
            .combinedClickable(
                onClick = {
                    onClick(item.customerId)
                },
                onLongClick = {
                    onLongClick(item.customerId)
                },
            ),
        headlineContent = {
            Text(
                text = item.customerPhone,
                style = MaterialTheme.typography.labelLarge,
            )
        },
        supportingContent = item.customerName?.let {
            {
                Text(
                    text = it,
                )
            }
        },
        leadingContent = {
            CircularBox(
                icon = PoposIcons.Person4,
                doesSelected = doesSelected(item.customerId),
                text = item.customerName,
            )
        },
        trailingContent = {
            Icon(
                PoposIcons.ArrowRightAlt,
                contentDescription = "Localized description",
            )
        },
        shadowElevation = 2.dp,
        tonalElevation = 2.dp,
        colors = ListItemDefaults.colors(
            containerColor = containerColor,
        ),
    )
}

@DevicePreviews
@Composable
private fun CustomerDataPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        CustomerData(
            modifier = modifier,
            item = Customer(
                customerId = 1,
                customerPhone = "9078563412",
                customerName = "New Customer",
                customerEmail = "new@gmail.com",
            ),
            doesSelected = { true },
            onClick = {},
            onLongClick = {},
        )
    }
}

@DevicePreviews
@Composable
private fun CustomersDataPreview(
    modifier: Modifier = Modifier,
    customers: List<Customer> = CustomerPreviewData.customerList
) {
    PoposRoomTheme {
        CustomersData(
            modifier = modifier,
            customers = customers,
            isInSelectionMode = true,
            doesSelected = { it % 2 == 0},
            onClickSelectItem = {},
            onNavigateToDetails = {},
        )
    }
}