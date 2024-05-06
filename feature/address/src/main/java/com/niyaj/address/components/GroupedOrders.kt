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

package com.niyaj.address.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toRupee
import com.niyaj.common.utils.toTime
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.AddressWiseOrder
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.StandardRoundedFilterChip

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GroupedOrders(
    customerPhone: String,
    orderDetails: List<AddressWiseOrder>,
    onClickOrder: (Int) -> Unit,
) = trace("Address::GroupedOrders") {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
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

        Spacer(modifier = Modifier.height(SpaceMini))
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceMini))

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.Center,
            maxItemsInEachRow = 2,
        ) {
            orderDetails.forEach { order ->
                StandardRoundedFilterChip(
                    text = order.totalPrice.toRupee,
                    icon = PoposIcons.Tag,
                    onClick = {
                        onClickOrder(order.orderId)
                    },
                )

                Spacer(modifier = Modifier.width(SpaceSmall))
            }
        }

        Spacer(modifier = Modifier.height(SpaceMini))
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
    }
}