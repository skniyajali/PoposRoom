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

package com.niyaj.addonitem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.AddOnTestTags.ADDON_ITEM_TAG
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.AddOnItem
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.drawAnimatedBorder
import com.niyaj.ui.utils.DevicePreviews

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun AddOnItemData(
    modifier: Modifier = Modifier,
    item: AddOnItem,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
) = trace("AddOnItemData") {
    val borderStroke = if (doesSelected(item.itemId)) border else null

    ElevatedCard(
        modifier = modifier
            .height(IntrinsicSize.Max)
            .testTag(ADDON_ITEM_TAG.plus(item.itemId))
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
                    onClick(item.itemId)
                },
                onLongClick = {
                    onLongClick(item.itemId)
                },
            ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
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
                Text(text = item.itemName)
                Spacer(modifier = Modifier.height(SpaceSmall))
                Text(text = item.itemPrice.toRupee)
            }

            CircularBox(
                icon = PoposIcons.Link,
                doesSelected = doesSelected(item.itemId),
                showBorder = !item.isApplicable,
            )
        }
    }
}


@Composable
@DevicePreviews
fun AddOnItemDataSelected(
    addOnItem: AddOnItem = AddOnItem(
        itemId = 1,
        itemName = "Extra Cheese",
        itemPrice = 100,
        isApplicable = true,
        createdAt = 1621537200000,
        updatedAt = null,
    )
) {
    AddOnItemData(
        item = addOnItem,
        doesSelected = { true },
        onClick = {},
        onLongClick = {},
    )
}

@Composable
@DevicePreviews
fun AddOnItemDataNotSelected(
    addOnItem: AddOnItem = AddOnItem(
        itemId = 1,
        itemName = "Extra Cheese",
        itemPrice = 100,
        isApplicable = true,
        createdAt = 1621537200000,
        updatedAt = null,
    )
) {
    AddOnItemData(
        item = addOnItem,
        doesSelected = { false },
        onClick = {},
        onLongClick = {},
    )
}