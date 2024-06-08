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

package com.niyaj.market.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.common.tags.MarketListTestTags
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toTimeSpan
import com.niyaj.designsystem.components.PoposOutlinedIconButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.MarketListWithType
import com.niyaj.model.MarketListWithTypes
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.TextWithIcon
import com.niyaj.ui.components.drawAnimatedBorder

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MarketListItemCard(
    items: MarketListWithTypes,
    doesSelected: (Int) -> Boolean,
    doesExpanded: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    onClickShare: (List<Int>) -> Unit,
    onClickPrint: (List<Int>) -> Unit,
    onClickViewDetails: (List<Int>) -> Unit,
    onClickManageList: (listTypeId: Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
) {
    val listIds = items.marketTypes.map { it.listWithTypeId }

    val marketId = items.marketList.marketId
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
                            shape = CardDefaults.elevatedShape,
                        )
                } ?: Modifier,
            )
            .clip(CardDefaults.elevatedShape),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            ListItem(
                headlineContent = {
                    Text(
                        text = items.marketList.marketDate.toPrettyDate(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                },
                overlineContent = {
                    Text(text = "Market Date")
                },
                leadingContent = {
                    CircularBox(
                        icon = PoposIcons.ShoppingBag,
                        doesSelected = false,
                    )
                },
                trailingContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            SpaceSmall,
                            Alignment.CenterHorizontally,
                        ),
                    ) {
                        PoposOutlinedIconButton(
                            icon = PoposIcons.Print,
                            onClick = {
                                onClickPrint(listIds)
                            },
                            enabled = items.marketTypes.isNotEmpty(),
                            borderColor = MaterialTheme.colorScheme.secondary,
                        )

                        PoposOutlinedIconButton(
                            icon = PoposIcons.Share,
                            onClick = {
                                onClickShare(listIds)
                            },
                            enabled = items.marketTypes.isNotEmpty(),
                            borderColor = MaterialTheme.colorScheme.secondary,
                        )

                        PoposOutlinedIconButton(
                            icon = PoposIcons.OpenInNew,
                            onClick = {
                                onClickViewDetails(listIds)
                            },
                            enabled = items.marketTypes.isNotEmpty(),
                            borderColor = MaterialTheme.colorScheme.secondary,
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
                ),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val size = items.marketTypes.size

                IconWithText(
                    text = "$size Market List",
                    icon = PoposIcons.Inbox,
                )

                TextWithIcon(
                    text = (
                        items.marketList.updatedAt
                            ?: items.marketList.createdAt
                        ).toTimeSpan,
                    icon = PoposIcons.AccessTime,
                    tintColor = Color.Gray,
                    textColor = Color.Gray,
                )
            }

            HorizontalDivider()

            AnimatedVisibility(
                visible = doesExpanded(items.marketList.marketId),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(SpaceMini))

                    items.marketTypes.forEachIndexed { index, listType ->
                        TypeList(
                            marketType = listType,
                            onClickManageList = onClickManageList,
                        )

                        if (index < items.marketTypes.size - 1) {
                            Spacer(modifier = Modifier.height(SpaceMini))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TypeList(
    modifier: Modifier = Modifier,
    marketType: MarketListWithType,
    onClickManageList: (listTypeId: Int) -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
        shape = RectangleShape,
        onClick = {
            onClickManageList(marketType.listWithTypeId)
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconWithText(
                text = marketType.typeName,
                icon = PoposIcons.Category,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = SpaceMini),
            )

            IconWithText(
                text = marketType.listType,
                icon = PoposIcons.ListAlt,
                style = TextStyle(
                    fontFamily = FontFamily.Cursive,
                    fontWeight = FontWeight.SemiBold,
                ),
                modifier = Modifier.weight(1.5f),
            )

            Icon(
                imageVector = PoposIcons.ArrowRightAlt,
                contentDescription = marketType.typeName,
                modifier = Modifier
                    .padding(SpaceSmall),
            )
        }
    }
}
