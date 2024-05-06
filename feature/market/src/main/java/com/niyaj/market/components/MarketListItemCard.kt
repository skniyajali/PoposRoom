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

package com.niyaj.market.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.common.tags.MarketListTestTags
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toTimeSpan
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.MarketListWithItems
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.StandardFilledTonalIconButton
import com.niyaj.ui.components.TextWithIcon
import com.niyaj.ui.components.drawAnimatedBorder

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MarketListItemCard(
    withItems: MarketListWithItems,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    onClickShare: (Int, Long) -> Unit,
    onClickPrint: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
) {
    val marketId = withItems.marketList.marketId
    val borderStroke = if (doesSelected(marketId)) border else null

    ElevatedCard(
        modifier = Modifier
            .testTag(MarketListTestTags.MARKET_LIST_ITEM_TAG.plus(marketId))
            .fillMaxWidth()
            .padding(SpaceSmall)
            .then(
                borderStroke?.let {
                    Modifier
                        .drawAnimatedBorder(
                            strokeWidth = 1.dp,
                            durationMillis = 2000,
                            shape = CardDefaults.elevatedShape
                        )
                } ?: Modifier
            )
            .clip(CardDefaults.elevatedShape),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            ListItem(
                headlineContent = {
                    Text(
                        text = withItems.marketList.marketDate.toPrettyDate(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                },
                leadingContent = {
                    CircularBox(
                        icon = PoposIcons.ShoppingBag,
                        doesSelected = false
                    )
                },
                trailingContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            SpaceSmall,
                            Alignment.CenterHorizontally
                        )
                    ) {
                        StandardFilledTonalIconButton(
                            icon = PoposIcons.Print,
                            onClick = {
                                onClickPrint(marketId)
                            },
                            enabled = withItems.items.isNotEmpty(),
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )

                        StandardFilledTonalIconButton(
                            icon = PoposIcons.Share,
                            onClick = {
                                onClickShare(marketId, withItems.marketList.marketDate)
                            },
                            enabled = withItems.items.isNotEmpty(),
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    }
                },
                modifier = Modifier.combinedClickable(
                    onClick = {
                        onClick(marketId)
                    },
                    onLongClick = {
                        onLongClick(marketId)
                    },
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconWithText(
                    text = "${withItems.items.size} Items",
                    icon = PoposIcons.Inbox
                )

                TextWithIcon(
                    text = (withItems.marketList.updatedAt
                        ?: withItems.marketList.createdAt).toTimeSpan,
                    icon = PoposIcons.AccessTime,
                    tintColor = Color.Gray,
                    textColor = Color.Gray
                )
            }
        }
    }
}