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

package com.niyaj.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.niyaj.designsystem.components.StandardFilterChip
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.model.EmployeeNameAndId

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CartDeliveryPartners(
    modifier: Modifier = Modifier,
    partners: List<EmployeeNameAndId>,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    selectedColor: Color = MaterialTheme.colorScheme.tertiary,
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor),
        horizontalArrangement = Arrangement.spacedBy(SpaceSmallMax, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.Center,
    ) {
        partners.forEach { partner ->
            key(partner.employeeId) {
                val icon = if (doesSelected(partner.employeeId)) PoposIcons.Done
                else PoposIcons.Person4

                StandardFilterChip(
                    text = partner.employeeName,
                    onClick = {
                        onClick(partner.employeeId)
                    },
                    selected = doesSelected(partner.employeeId),
                    icon = icon,
                    selectedColor = selectedColor,
                    textStyle = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}