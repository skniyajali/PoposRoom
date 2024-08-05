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

package com.niyaj.address.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Address
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.parameterProvider.AddressPreviewData
import com.niyaj.ui.utils.DevicePreviews

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun AddressData(
    modifier: Modifier = Modifier,
    item: Address,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
    containerColor: Color = MaterialTheme.colorScheme.background,
) = trace("Address::Data") {
    val borderStroke = if (doesSelected(item.addressId)) border else null

    ElevatedCard(
        modifier = modifier
            .semantics { selected = doesSelected(item.addressId) }
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .padding(SpaceSmall)
            .then(
                borderStroke?.let {
                    Modifier.border(it, CardDefaults.elevatedShape)
                } ?: Modifier,
            )
            .combinedClickable(
                onClick = {
                    onClick(item.addressId)
                },
                onLongClick = {
                    onLongClick(item.addressId)
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
                modifier = Modifier.weight(2.5f, false),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = item.addressName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(SpaceSmall))
                Text(text = item.shortName)
            }

            Spacer(modifier.width(SpaceSmall))

            CircularBox(
                icon = PoposIcons.Address,
                doesSelected = doesSelected(item.addressId),
                text = item.addressName,
            )
        }
    }
}

@DevicePreviews
@Composable
private fun AddressDataPreview(
    item: Address = AddressPreviewData.addressList.first(),
) {
    PoposRoomTheme {
        AddressData(
            modifier = Modifier,
            item = item,
            doesSelected = { false },
            onClick = {},
            onLongClick = {},
        )
    }
}

@DevicePreviews
@Composable
private fun AddressDataSelectedPreview(
    item: Address = AddressPreviewData.addressList.last(),
) {
    PoposRoomTheme {
        AddressData(
            modifier = Modifier,
            item = item,
            doesSelected = { true },
            onClick = {},
            onLongClick = {},
        )
    }
}
