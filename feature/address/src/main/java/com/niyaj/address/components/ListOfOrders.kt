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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toRupee
import com.niyaj.common.utils.toTime
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.AddressWiseOrder
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.parameterProvider.AddressPreviewData
import com.niyaj.ui.utils.DevicePreviews

@Composable
internal fun ListOfOrders(
    modifier: Modifier = Modifier,
    orderSize: Int,
    orderDetails: List<AddressWiseOrder>,
    onClickOrder: (Int) -> Unit,
) = trace("Address::ListOfOrder") {
    Surface(modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            orderDetails.forEachIndexed { index, order ->
                key(order.orderId) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            onClickOrder(order.orderId)
                        },
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceSmall),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            IconWithText(
                                text = "${order.orderId}",
                                icon = PoposIcons.Tag,
                                tintColor = MaterialTheme.colorScheme.tertiary,
                                isTitle = true,
                            )

                            Column {
                                Text(
                                    text = order.customerPhone,
                                    textAlign = TextAlign.Start,
                                )

                                order.customerName?.let {
                                    Spacer(
                                        modifier = Modifier.height(
                                            SpaceMini,
                                        ),
                                    )
                                    Text(text = it)
                                }
                            }

                            Text(
                                text = order.totalPrice.toRupee,
                                textAlign = TextAlign.Start,
                                fontWeight = FontWeight.SemiBold,
                            )

                            Text(
                                text = order.updatedAt.toTime,
                                textAlign = TextAlign.End,
                            )
                        }
                    }
                }

                if (index != orderSize - 1) {
                    Spacer(modifier = Modifier.height(SpaceMini))
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(SpaceMini))
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun ListOfOrdersPreview(
    modifier: Modifier = Modifier,
    orders: List<AddressWiseOrder> = AddressPreviewData.sampleAddressWiseOrders,
) {
    PoposRoomTheme {
        ListOfOrders(
            modifier = modifier,
            orderSize = orders.size,
            orderDetails = orders,
            onClickOrder = {},
        )
    }
}
