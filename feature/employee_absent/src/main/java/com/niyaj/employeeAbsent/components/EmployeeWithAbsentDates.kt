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

package com.niyaj.employeeAbsent.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_TAG
import com.niyaj.common.utils.toDate
import com.niyaj.common.utils.toMonthAndYear
import com.niyaj.designsystem.components.StandardAssistChip
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Absent
import com.niyaj.model.EmployeeWithAbsents
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.StandardElevatedCard
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.TextWithBorderCount
import com.niyaj.ui.parameterProvider.AbsentPreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.TrackScrollJank

@Composable
internal fun AbsentEmployeeList(
    modifier: Modifier = Modifier,
    items: List<EmployeeWithAbsents>,
    showTrailingIcon: Boolean = false,
    expanded: (Int) -> Boolean,
    onExpandChanged: (Int) -> Unit,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    onChipClick: (Int) -> Unit = {},
    lazyListState: LazyListState = rememberLazyListState(),
) {
    TrackScrollJank(scrollableState = lazyListState, stateName = "Absent::List")

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(SpaceSmall),
        verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        state = lazyListState,
    ) {
        items(
            items = items,
            key = { it.employee.employeeId },
        ) { item ->
            if (item.absents.isNotEmpty()) {
                EmployeeWithAbsentDates(
                    item = item,
                    showTrailingIcon = showTrailingIcon,
                    expanded = expanded,
                    doesSelected = doesSelected,
                    onClick = onClick,
                    onExpandChanged = onExpandChanged,
                    onLongClick = onLongClick,
                    onChipClick = onChipClick,
                )
            }
        }
    }
}

@Composable
private fun EmployeeWithAbsentDates(
    modifier: Modifier = Modifier,
    item: EmployeeWithAbsents,
    expanded: (Int) -> Boolean,
    onExpandChanged: (Int) -> Unit,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    onChipClick: (Int) -> Unit = {},
    showTrailingIcon: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.background,
) = trace("AbsentData") {
    val groupByMonth = remember(item.absents) {
        item.absents.groupBy { toMonthAndYear(it.absentDate) }
    }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceMini),
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
        ),
    ) {
        StandardExpandable(
            modifier = Modifier.padding(vertical = SpaceSmall),
            expanded = expanded(item.employee.employeeId),
            onExpandChanged = {
                onExpandChanged(item.employee.employeeId)
            },
            title = {
                IconWithText(
                    text = item.employee.employeeName,
                    icon = PoposIcons.Person,
                    isTitle = true,
                    tintColor = MaterialTheme.colorScheme.secondary,
                )
            },
            trailing = {
                if (showTrailingIcon) {
                    StandardAssistChip(
                        modifier = Modifier.wrapContentSize(),
                        text = "Add Entry",
                        icon = PoposIcons.Add,
                        onClick = { onChipClick(item.employee.employeeId) },
                    )
                }
            },
            content = {
                EmployeeAbsentData(
                    groupedAbsents = groupByMonth,
                    doesSelected = doesSelected,
                    onClick = onClick,
                    onLongClick = onLongClick,
                )
            },
        )
    }
}

/**
 * Employee Absent Dates
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EmployeeAbsentData(
    modifier: Modifier = Modifier,
    groupedAbsents: Map<String, List<Absent>>,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
) = trace("EmployeeAbsentData") {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        groupedAbsents.forEach { grouped ->
            TextWithBorderCount(
                modifier = Modifier,
                text = grouped.key,
                leadingIcon = PoposIcons.CalenderMonth,
                count = grouped.value.size,
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
                horizontalArrangement = Arrangement.spacedBy(SpaceMini),
                verticalArrangement = Arrangement.spacedBy(SpaceMini),
            ) {
                grouped.value.forEach { item ->
                    StandardElevatedCard(
                        modifier = modifier,
                        containerColor = MaterialTheme.colorScheme.background,
                        elevation = 1.dp,
                        testTag = ABSENT_TAG.plus(item.absentId),
                        doesSelected = doesSelected(item.absentId),
                        onClick = {
                            onClick(item.absentId)
                        },
                        onLongClick = {
                            onLongClick(item.absentId)
                        },
                    ) {
                        Text(
                            text = item.absentDate.toDate,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .padding(SpaceSmall),
                        )
                    }
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun AbsentEmployeeListPreview(
    modifier: Modifier = Modifier,
    items: List<EmployeeWithAbsents> = AbsentPreviewData.employeesWithAbsents,
) {
    PoposRoomTheme {
        AbsentEmployeeList(
            modifier = modifier,
            items = items,
            expanded = { true },
            onExpandChanged = {},
            doesSelected = { false },
            onClick = {},
            onLongClick = {},
            onChipClick = {},
        )
    }
}
