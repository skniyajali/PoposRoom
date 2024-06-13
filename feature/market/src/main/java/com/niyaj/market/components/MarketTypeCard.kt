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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.MarketTypeTags.MARKET_TYPE_TAG
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.utils.drawAnimatedBorder
import com.niyaj.model.MarketType
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.SettingsCard
import com.niyaj.ui.parameterProvider.MarketTypePreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.TrackScrollJank
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun MarketTypeList(
    modifier: Modifier = Modifier,
    items: ImmutableList<MarketType>,
    isInSelectionMode: Boolean,
    doesSelected: (Int) -> Boolean,
    onSelectItem: (Int) -> Unit,
    showCreateNewItem: Boolean = false,
    onCreateMarketItem: () -> Unit = {},
    lazyGridState: LazyGridState = rememberLazyGridState(),
) {
    TrackScrollJank(
        scrollableState = lazyGridState,
        stateName = "MarketTypes::List",
    )

    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(SpaceSmall),
        verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        horizontalArrangement = Arrangement.spacedBy(SpaceSmall),
        columns = GridCells.Fixed(2),
        state = lazyGridState,
    ) {
        if (showCreateNewItem) {
            item(span = { GridItemSpan(2) }) {
                SettingsCard(
                    modifier = Modifier,
                    title = "Create New Item",
                    subtitle = "",
                    icon = PoposIcons.Dns,
                    onClick = onCreateMarketItem,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    leadingColor = MaterialTheme.colorScheme.background,
                )
            }
        }

        items(
            items = items,
            key = { it.typeId },
        ) { item: MarketType ->
            MarketTypeCard(
                item = item,
                doesSelected = doesSelected,
                onClick = {
                    if (isInSelectionMode) {
                        onSelectItem(it)
                    }
                },
                onLongClick = onSelectItem,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MarketTypeCard(
    modifier: Modifier = Modifier,
    item: MarketType,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
    containerColor: Color = MaterialTheme.colorScheme.background,
) = trace("MarketTypeCard") {
    val borderStroke = if (doesSelected(item.typeId)) border else null

    ElevatedCard(
        shape = RoundedCornerShape(SpaceMini),
        modifier = modifier
            .testTag(MARKET_TYPE_TAG.plus(item.typeId))
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
            .combinedClickable(
                onClick = {
                    onClick(item.typeId)
                },
                onLongClick = {
                    onLongClick(item.typeId)
                },
            ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
        ),
        colors = CardDefaults.elevatedCardColors().copy(
            containerColor = containerColor,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.spacedBy(SpaceSmall, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularBox(
                icon = PoposIcons.Category,
                doesSelected = doesSelected(item.typeId),
            )

            Text(
                text = item.typeName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@DevicePreviews
@Composable
private fun MarketTypeListPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        MarketTypeList(
            modifier = modifier,
            items = MarketTypePreviewData.marketTypes.toImmutableList(),
            isInSelectionMode = false,
            doesSelected = { it % 2 == 0 },
            onSelectItem = {},
            showCreateNewItem = true,
            onCreateMarketItem = {},
        )
    }
}
