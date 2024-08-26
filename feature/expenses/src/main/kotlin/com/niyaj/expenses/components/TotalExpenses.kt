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

package com.niyaj.expenses.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.components.PoposOutlinedAssistChip
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.NoteText
import com.niyaj.ui.utils.DevicePreviews
import kotlinx.datetime.Clock

@Composable
fun TotalExpenses(
    totalAmount: String,
    totalItem: String,
    selectedDate: String,
    onDateClick: () -> Unit,
    modifier: Modifier = Modifier,
) = trace("TotalExpenses") {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(SpaceSmall),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            horizontalAlignment = Alignment.Start,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularBox(
                        icon = PoposIcons.TrendingUp,
                        selected = false,
                    )
                    Spacer(modifier = Modifier.width(SpaceSmall))
                    Text(
                        text = "Total Expenses",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                PoposOutlinedAssistChip(
                    text = selectedDate.toPrettyDate(),
                    icon = PoposIcons.CalenderMonth,
                    trailingIcon = PoposIcons.ArrowDown,
                    onClick = onDateClick,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmallMax))
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceSmallMax))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = totalAmount.toRupee,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                NoteText(
                    text = "Total $totalItem Expenses",
                    icon = PoposIcons.TrendingUp,
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun TotalExpensesPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        TotalExpenses(
            totalAmount = "10000",
            totalItem = "10",
            selectedDate = Clock.System.now().toEpochMilliseconds().toString(),
            onDateClick = {},
            modifier = modifier,
        )
    }
}
