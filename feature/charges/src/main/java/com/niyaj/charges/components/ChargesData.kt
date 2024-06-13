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

package com.niyaj.charges.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.ChargesTestTags.CHARGES_TAG
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Charges
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.StandardElevatedCard
import com.niyaj.ui.utils.DevicePreviews

@Composable
internal fun ChargesData(
    modifier: Modifier = Modifier,
    item: Charges,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
) = trace("ChargesData") {
    StandardElevatedCard(
        modifier = modifier
            .height(IntrinsicSize.Max),
        testTag = CHARGES_TAG.plus(item.chargesId),
        doesSelected = doesSelected(item.chargesId),
        onClick = {
            onClick(item.chargesId)
        },
        onLongClick = {
            onLongClick(item.chargesId)
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(2.5f, true),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = item.chargesName,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(SpaceSmall))
                Text(text = item.chargesPrice.toRupee)
            }

            Spacer(modifier = Modifier.width(SpaceSmall))

            CircularBox(
                icon = PoposIcons.Bolt,
                doesSelected = doesSelected(item.chargesId),
                showBorder = !item.isApplicable,
            )
        }
    }
}

@DevicePreviews
@Composable
private fun ChargesDataPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        ChargesData(
            modifier = modifier,
            item = Charges(
                chargesId = 1,
                chargesName = "New Charges check for clipping data in the card",
                chargesPrice = 10,
                isApplicable = true,
            ),
            doesSelected = { true },
            onClick = {},
            onLongClick = {},
        )
    }
}
