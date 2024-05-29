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

package com.niyaj.order.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toTime
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Order
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.StandardFilledTonalIconButton

@Composable
fun OrderedItemData(
    modifier: Modifier = Modifier,
    shape: Shape,
    order: Order,
    onClickPrintOrder: (Int) -> Unit,
    onClickShareOrder: (Int) -> Unit,
    containerColor: Color,
) = trace("OrderedItemData") {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconWithText(
                        text = order.orderId.toString(),
                        icon = PoposIcons.OutlinedTag,
                    )

                    order.customerPhone?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        IconWithText(
                            text = it,
                            icon = PoposIcons.OutlinedPhoneAndroid,
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    IconWithText(
                        text = order.orderDate.toTime,
                        icon = PoposIcons.OutlinedAccessTime,
                    )
                }

                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    order.customerAddress?.let {
                        IconWithText(
                            text = it,
                            icon = PoposIcons.OutlinedPlace,
                        )
                        Spacer(modifier = Modifier.height(SpaceSmall))
                    }

                    IconWithText(
                        text = order.orderPrice.toString(),
                        icon = PoposIcons.OutlinedCurrencyRupee,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SpaceMini),
                ) {
                    StandardFilledTonalIconButton(
                        icon = PoposIcons.OutlinedShare,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.tertiary,
                        onClick = { onClickShareOrder(order.orderId) },
                    )

                    StandardFilledTonalIconButton(
                        icon = PoposIcons.OutlinedPrint,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.secondary,
                        onClick = { onClickPrintOrder(order.orderId) },
                    )
                }
            }
        }
    }
}
