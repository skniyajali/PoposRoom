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

package com.niyaj.product.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.ProductTestTags
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Product
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.NoteText

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductCard(
    modifier: Modifier = Modifier,
    item: Product,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    showArrow: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.background,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
) = trace("ProductCard") {
    val borderStroke = if (doesSelected(item.productId)) border else null

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(ProductTestTags.PRODUCT_TAG.plus(item.productId))
            .padding(SpaceSmall)
            .then(
                borderStroke?.let {
                    Modifier.border(it, RoundedCornerShape(SpaceMini))
                } ?: Modifier,
            )
            .clip(RoundedCornerShape(SpaceMini))
            .combinedClickable(
                onClick = {
                    onClick(item.productId)
                },
                onLongClick = {
                    onLongClick(item.productId)
                },
            ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            ListItem(
                modifier = modifier
                    .fillMaxWidth(),
                headlineContent = {
                    Text(
                        text = item.productName,
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
                supportingContent = {
                    Text(text = item.productPrice.toRupee)
                },
                leadingContent = {
                    CircularBox(
                        icon = PoposIcons.Person,
                        doesSelected = doesSelected(item.productId),
                        text = item.productName,
                        showBorder = !item.productAvailability,
                    )
                },
                trailingContent = {
                    if (showArrow) {
                        Icon(
                            imageVector = PoposIcons.ArrowRightAlt,
                            contentDescription = "View ${item.productName} Details",
                        )
                    }
                },
                colors = ListItemDefaults.colors(
                    containerColor = containerColor,
                ),
            )

            if (item.productDescription.isNotEmpty()) {
                NoteText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall),
                    text = item.productDescription,
                    icon = PoposIcons.TurnedInNot,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
