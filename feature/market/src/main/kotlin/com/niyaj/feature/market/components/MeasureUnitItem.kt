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

package com.niyaj.feature.market.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_LIST_ITEM_TAG
import com.niyaj.common.utils.safeString
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.utils.drawAnimatedBorder
import com.niyaj.model.MeasureUnit
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.parameterProvider.MeasureUnitPreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.TrackScrollJank
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun MeasureUnitItems(
    items: ImmutableList<MeasureUnit>,
    isInSelectionMode: Boolean,
    doesSelected: (Int) -> Boolean,
    onSelectItem: (Int) -> Unit,
    modifier: Modifier = Modifier,
    lazyGridState: LazyGridState = rememberLazyGridState(),
) {
    TrackScrollJank(scrollableState = lazyGridState, stateName = "MeasureUnit::List")

    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(SpaceMedium),
        verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        horizontalArrangement = Arrangement.spacedBy(SpaceSmall),
        columns = GridCells.Fixed(2),
        state = lazyGridState,
    ) {
        items(
            items = items,
            key = { it.unitId },
        ) { item: MeasureUnit ->
            MeasureUnitItem(
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
fun MeasureUnitItem(
    item: MeasureUnit,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
    containerColor: Color = MaterialTheme.colorScheme.background,
) = trace("MeasureUnitItem") {
    val borderStroke = if (doesSelected(item.unitId)) border else null

    ElevatedCard(
        modifier = modifier
            .testTag(UNIT_LIST_ITEM_TAG.plus(item.unitId))
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
                    onClick(item.unitId)
                },
                onLongClick = {
                    onLongClick(item.unitId)
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
                .fillMaxSize()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = item.unitName,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
                Text(text = item.unitValue.safeString)
            }

            CircularBox(
                icon = PoposIcons.MonitorWeight,
                selected = doesSelected(item.unitId),
            )
        }
    }
}

@DevicePreviews
@Composable
private fun MeasureUnitItemsPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        MeasureUnitItems(
            items = MeasureUnitPreviewData.measureUnits.toImmutableList(),
            isInSelectionMode = false,
            doesSelected = { it % 2 == 0 },
            onSelectItem = {},
            modifier = modifier,
        )
    }
}
