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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.MarketListTestTags
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceLarge
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.model.MarketList
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardOutlinedAssistChip


@Composable
fun MarketListItemHeader(
    marketList: MarketList,
    selectedDate: String,
    onClickDate: () -> Unit,
    onClickSaveChanges: () -> Unit,
) = trace("MarketListItemHeader") {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(SpaceSmall)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmallMax)
                .padding(vertical = SpaceSmall),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(SpaceLarge)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SpaceSmall)
                ) {
                    CircularBox(
                        icon = PoposIcons.CalenderMonth,
                        doesSelected = false,
                    )

                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(SpaceMini)
                    ) {
                        Text(
                            text = "Market Date".uppercase(),
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = marketList.marketDate.toPrettyDate(),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }

                StandardOutlinedAssistChip(
                    text = if (selectedDate.toPrettyDate() == marketList.marketDate.toPrettyDate()) "Change" else selectedDate.toPrettyDate(),
                    icon = PoposIcons.CalenderMonth,
                    onClick = onClickDate,
                    trailingIcon = PoposIcons.ArrowDropDown
                )
            }

            AnimatedVisibility(
                visible = selectedDate.toPrettyDate() != marketList.marketDate.toPrettyDate()
            ) {
                StandardButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(MarketListTestTags.ADD_EDIT_MARKET_LIST_BUTTON),
                    text = "Save Changes",
                    icon = PoposIcons.EditCalender,
                    enabled = true,
                    onClick = onClickSaveChanges
                )
            }
        }
    }
}