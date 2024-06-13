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

package com.niyaj.employee.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_TAG
import com.niyaj.designsystem.components.StandardAssistChip
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.model.Employee
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.parameterProvider.EmployeePreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.TrackScrollJank
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun EmployeeList(
    modifier: Modifier = Modifier,
    employees: ImmutableList<Employee>,
    isInSelectionMode: Boolean,
    doesSelected: (Int) -> Boolean,
    onSelectItem: (Int) -> Unit,
    onNavigateToDetails: (Int) -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    TrackScrollJank(scrollableState = lazyListState, stateName = "Employee::List")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("employeeList"),
        contentPadding = PaddingValues(SpaceMedium),
        verticalArrangement = Arrangement.spacedBy(SpaceMedium),
        state = lazyListState,
    ) {
        items(
            items = employees,
            key = { it.employeeId },
        ) { item: Employee ->
            EmployeeData(
                item = item,
                doesSelected = doesSelected,
                onClick = {
                    if (isInSelectionMode) {
                        onSelectItem(it)
                    } else {
                        onNavigateToDetails(it)
                    }
                },
                onLongClick = onSelectItem,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmployeeData(
    modifier: Modifier = Modifier,
    item: Employee,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
    containerColor: Color = MaterialTheme.colorScheme.background,
) = trace("EmployeeData") {
    val borderStroke = if (doesSelected(item.employeeId)) border else null

    ListItem(
        modifier = modifier
            .testTag(EMPLOYEE_TAG.plus(item.employeeId))
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(SpaceMini))
            .then(
                borderStroke?.let {
                    Modifier.border(it, RoundedCornerShape(SpaceMini))
                } ?: Modifier,
            )
            .combinedClickable(
                onClick = {
                    onClick(item.employeeId)
                },
                onLongClick = {
                    onLongClick(item.employeeId)
                },
            ),
        headlineContent = {
            Text(
                text = item.employeeName,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Text(
                text = item.employeePhone,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingContent = {
            CircularBox(
                icon = PoposIcons.Person,
                doesSelected = doesSelected(item.employeeId),
                text = item.employeeName,
            )
        },
        trailingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SpaceMini),
            ) {
                if (item.isDeliveryPartner) {
                    StandardAssistChip(
                        text = "Delivery Partner",
                        icon = PoposIcons.DeliveryDining,
                    )
                }

                Icon(
                    PoposIcons.ArrowRightAlt,
                    contentDescription = "Localized description",
                )
            }
        },
        shadowElevation = 2.dp,
        tonalElevation = 2.dp,
        colors = ListItemDefaults.colors(
            containerColor = containerColor,
        ),
    )
}

@DevicePreviews
@Composable
private fun EmployeeDataPreview(
    modifier: Modifier = Modifier,
    employee: Employee = EmployeePreviewData.employeeList.first(),
) {
    PoposRoomTheme {
        EmployeeData(
            modifier = modifier,
            item = employee,
            doesSelected = { true },
            onClick = {},
            onLongClick = {},
        )
    }
}

@DevicePreviews
@Composable
private fun EmployeeListPreview(
    modifier: Modifier = Modifier,
    employees: ImmutableList<Employee> = EmployeePreviewData.employeeList.toImmutableList(),
) {
    PoposRoomTheme {
        EmployeeList(
            modifier = modifier,
            employees = employees,
            isInSelectionMode = false,
            doesSelected = { it % 2 == 0 },
            onSelectItem = {},
            onNavigateToDetails = {},
        )
    }
}
