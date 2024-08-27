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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Order
import de.charlex.compose.RevealSwipe

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OrderedItem(
    order: Order,
    onClickPrintOrder: (Int) -> Unit,
    onMarkedAsProcessing: (Int) -> Unit,
    onClickDelete: (Int) -> Unit,
    onClickViewDetails: (Int) -> Unit,
    onClickEdit: (Int) -> Unit,
    onClickShareOrder: (Int) -> Unit,
    modifier: Modifier = Modifier,
) = trace("OrderedItem") {
    RevealSwipe(
        modifier = modifier
            .fillMaxWidth(),
        onContentClick = {
            onClickViewDetails(order.orderId)
        },
        maxRevealDp = 150.dp,
        hiddenContentStart = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
                horizontalArrangement = Arrangement.spacedBy(SpaceSmall, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = {
                        onMarkedAsProcessing(order.orderId)
                    },
                ) {
                    Icon(
                        imageVector = PoposIcons.Restore,
                        contentDescription = "Mark as processing",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp),
                    )
                }

                IconButton(
                    onClick = {
                        onClickEdit(order.orderId)
                    },
                ) {
                    Icon(
                        imageVector = PoposIcons.Edit,
                        contentDescription = "Edit Order",
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        },
        hiddenContentEnd = {
            IconButton(
                onClick = {
                    onClickDelete(order.orderId)
                },
                modifier = Modifier.padding(horizontal = 25.dp),
            ) {
                Icon(
                    imageVector = PoposIcons.Delete,
                    contentDescription = "Delete order",
                    tint = MaterialTheme.colorScheme.onSecondary,
                )
            }
        },
        animateBackgroundCardColor = true,
        contentColor = MaterialTheme.colorScheme.onBackground,
        backgroundCardContentColor = MaterialTheme.colorScheme.background,
        backgroundCardStartColor = MaterialTheme.colorScheme.primary,
        backgroundCardEndColor = MaterialTheme.colorScheme.secondary,
        shape = RoundedCornerShape(6.dp),
        backgroundStartActionLabel = "Start",
        backgroundEndActionLabel = "End",
    ) {
        OrderedItemData(
            shape = it,
            order = order,
            onClickPrintOrder = onClickPrintOrder,
            onClickShareOrder = onClickShareOrder,
            containerColor = MaterialTheme.colorScheme.background,
        )
    }
}
