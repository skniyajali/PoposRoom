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

package com.niyaj.address.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toRupee
import com.niyaj.common.utils.toTime
import com.niyaj.designsystem.components.StandardRoundedFilterChip
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.AddressWiseOrder
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.parameterProvider.AddressPreviewData
import com.niyaj.ui.utils.DevicePreviews

@Composable
internal fun GroupedOrders(
    modifier: Modifier = Modifier,
    customerPhone: String,
    orderDetails: List<AddressWiseOrder>,
    onClickOrder: (Int) -> Unit,
) = trace("Address::GroupedOrders") {
    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconWithText(
                text = customerPhone,
                icon = PoposIcons.PhoneAndroid,
            )

            val startDate = orderDetails.first().updatedAt
            val endDate = orderDetails.last().updatedAt

            Row(
                modifier = Modifier
                    .padding(SpaceMini),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = endDate.toTime,
                    style = MaterialTheme.typography.labelMedium,
                )

                if (endDate.toTime != startDate.toTime) {
                    Spacer(modifier = Modifier.width(SpaceMini))
                    Icon(
                        imageVector = PoposIcons.ArrowRightAlt,
                        contentDescription = "DatePeriod",
                    )
                    Spacer(modifier = Modifier.width(SpaceMini))
                    Text(
                        text = startDate.toTime,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        GroupedOrderDetails(
            orderDetails = orderDetails,
            onClickOrder = onClickOrder,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GroupedOrderDetails(
    modifier: Modifier = Modifier,
    orderDetails: List<AddressWiseOrder>,
    onClickOrder: (Int) -> Unit,
) = trace("GroupedOrderDetails") {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = SpaceSmall),
        horizontalArrangement = Arrangement.spacedBy(SpaceSmall, Alignment.Start),
        verticalArrangement = Arrangement.Center,
    ) {
        orderDetails.forEach { order ->
            StandardRoundedFilterChip(
                text = order.totalPrice.toRupee,
                icon = PoposIcons.Tag,
                onClick = {
                    onClickOrder(order.orderId)
                },
            )
        }
    }
}

@DevicePreviews
@Composable
private fun GroupedOrdersPreview(
    modifier: Modifier = Modifier,
    orderDetails: List<AddressWiseOrder> = AddressPreviewData.groupedAddressWiseOrder,
) {
    PoposRoomTheme {
        Surface {
            Column {
                orderDetails.groupBy { it.customerPhone }.forEach { (phone, orders) ->
                    GroupedOrders(
                        customerPhone = phone,
                        orderDetails = orders,
                        onClickOrder = {},
                    )
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun GroupedOrderDetailsPreview(
    modifier: Modifier = Modifier,
    orderDetails: List<AddressWiseOrder> = AddressPreviewData.groupedAddressWiseOrder,
) {
    PoposRoomTheme {
        GroupedOrderDetails(
            modifier = modifier,
            orderDetails = orderDetails,
            onClickOrder = {},
        )
    }
}
