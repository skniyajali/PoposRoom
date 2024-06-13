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

package com.niyaj.cartorder.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_ITEM_TAG
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.CartOrder
import com.niyaj.model.OrderStatus
import com.niyaj.model.OrderType
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.StandardChip
import com.niyaj.ui.parameterProvider.CartOrderOrderTypeDataPreviewParameter
import com.niyaj.ui.utils.DevicePreviews

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CartOrderData(
    modifier: Modifier = Modifier,
    item: CartOrder,
    doesSelected: (Int) -> Boolean,
    orderSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
    containerColor: Color = MaterialTheme.colorScheme.background,
) = trace("CartOrderData") {
    val borderStroke = if (doesSelected(item.orderId)) border else null

    ElevatedCard(
        modifier = modifier
            .height(IntrinsicSize.Max)
            .testTag(CART_ORDER_ITEM_TAG.plus(item.orderId))
            .then(
                borderStroke?.let {
                    Modifier.border(it, CardDefaults.elevatedShape)
                } ?: Modifier,
            )
            .combinedClickable(
                onClick = {
                    onClick(item.orderId)
                },
                onLongClick = {
                    onLongClick(item.orderId)
                },
            ),
        colors = CardDefaults.elevatedCardColors().copy(
            containerColor = containerColor,
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularBox(
                modifier = Modifier.padding(SpaceSmall),
                icon = PoposIcons.Tag,
                doesSelected = doesSelected(item.orderId),
                showBorder = orderSelected(item.orderId),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        buildAnnotatedString {
                            if (item.orderType == OrderType.DineOut) {
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.Red,
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                ) {
                                    append(item.address.shortName.uppercase())

                                    append(" - ")
                                }
                            }

                            append(item.orderId.toString())
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Text(
                        text = item.orderType.name,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }

                if (item.orderStatus == OrderStatus.PLACED) {
                    StandardChip(
                        text = item.orderStatus.name,
                        isClickable = false,
                    )
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun CartOrderDataPreview(
    @PreviewParameter(CartOrderOrderTypeDataPreviewParameter::class)
    cartOrder: CartOrder,
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        CartOrderData(
            modifier = modifier,
            item = cartOrder,
            doesSelected = { true },
            orderSelected = { false },
            onClick = {},
            onLongClick = {},
        )
    }
}
