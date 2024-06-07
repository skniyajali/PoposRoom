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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_LIST_ITEM_TAG
import com.niyaj.common.utils.safeString
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.utils.drawAnimatedBorder
import com.niyaj.model.MeasureUnit
import com.niyaj.ui.components.CircularBox

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MeasureUnitItem(
    modifier: Modifier = Modifier,
    item: MeasureUnit,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
) = trace("MeasureUnitItem") {
    val borderStroke = if (doesSelected(item.unitId)) border else null

    ElevatedCard(
        modifier = modifier
            .testTag(UNIT_LIST_ITEM_TAG.plus(item.unitId))
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
            .clip(CardDefaults.elevatedShape)
            .combinedClickable(
                onClick = {
                    onClick(item.unitId)
                },
                onLongClick = {
                    onLongClick(item.unitId)
                },
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
                doesSelected = doesSelected(item.unitId),
            )
        }
    }
}
