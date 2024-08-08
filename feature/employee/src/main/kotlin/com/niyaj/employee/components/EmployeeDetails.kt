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

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toDate
import com.niyaj.common.utils.toJoinedDate
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Employee
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailableHalf
import com.niyaj.ui.components.LoadingIndicatorHalf
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.EmployeePreviewParameter
import com.niyaj.ui.utils.DevicePreviews

/**
 *
 */
@Composable
internal fun EmployeeDetails(
    employeeState: UiState<Employee>,
    modifier: Modifier = Modifier,
    employeeDetailsExpanded: Boolean = false,
    onClickEdit: () -> Unit = {},
    onExpanded: () -> Unit = {},
) = trace("EmployeeDetails") {
    Card(
        onClick = onExpanded,
        modifier = modifier
            .testTag("EmployeeDetailsCard")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 1.dp,
        ),
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = employeeDetailsExpanded,
            onExpandChanged = {
                onExpanded()
            },
            title = {
                IconWithText(
                    text = "Employee Details",
                    icon = PoposIcons.Person,
                )
            },
            trailing = {
                IconButton(
                    onClick = onClickEdit,
                ) {
                    Icon(
                        imageVector = PoposIcons.Edit,
                        contentDescription = "Edit Employee",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = onExpanded,
                ) {
                    Icon(
                        imageVector = PoposIcons.ArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                }
            },
            content = {
                Crossfade(
                    targetState = employeeState,
                    label = "EmployeeDetailsState",
                ) { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicatorHalf()

                        is UiState.Empty -> {
                            ItemNotAvailableHalf(
                                text = "Employee details not found",
                                showImage = false,
                            )
                        }

                        is UiState.Success -> {
                            Column(
                                modifier = Modifier
                                    .testTag("EmployeeDetailsContent")
                                    .fillMaxWidth()
                                    .padding(SpaceSmall),
                                verticalArrangement = Arrangement.spacedBy(SpaceSmall)
                            ) {
                                IconWithText(
                                    modifier = Modifier.testTag(state.data.employeeName),
                                    text = "Name - ${state.data.employeeName}",
                                    icon = PoposIcons.Person,
                                )

                                IconWithText(
                                    modifier = Modifier.testTag(state.data.employeePhone),
                                    text = "Phone - ${state.data.employeePhone}",
                                    icon = PoposIcons.PhoneAndroid,
                                )
                                state.data.employeeEmail?.let {
                                    IconWithText(
                                        modifier = Modifier.testTag(it),
                                        text = "Email - ${state.data.employeeEmail}",
                                        icon = PoposIcons.Email,
                                    )
                                }

                                IconWithText(
                                    modifier = Modifier.testTag(state.data.employeeSalary.toRupee),
                                    text = "Salary - ${state.data.employeeSalary.toRupee}",
                                    icon = PoposIcons.Rupee,
                                )

                                IconWithText(
                                    modifier = Modifier.testTag(state.data.employeeSalaryType.name),
                                    text = "Salary Type - ${state.data.employeeSalaryType}",
                                    icon = PoposIcons.Merge,
                                )

                                IconWithText(
                                    modifier = Modifier.testTag(state.data.employeePosition),
                                    text = "Position - ${state.data.employeePosition}",
                                    icon = PoposIcons.Approval,
                                )

                                IconWithText(
                                    modifier = Modifier.testTag(state.data.employeeType.name),
                                    text = "Type - ${state.data.employeeType}",
                                    icon = PoposIcons.MergeType,
                                )

                                IconWithText(
                                    modifier = Modifier.testTag(state.data.employeeJoinedDate.toDate),
                                    text = "Joined Date : ${state.data.employeeJoinedDate.toJoinedDate}",
                                    icon = PoposIcons.CalenderMonth,
                                )

                                IconWithText(
                                    text = "Created At : ${state.data.createdAt.toPrettyDate()}",
                                    icon = PoposIcons.AccessTime,
                                )
                                state.data.updatedAt?.let {
                                    IconWithText(
                                        text = "Updated At : ${it.toPrettyDate()}",
                                        icon = PoposIcons.Login,
                                    )
                                }
                            }
                        }
                    }
                }
            },
        )
    }
}

@DevicePreviews
@Composable
private fun EmployeeDetailsPreview(
    @PreviewParameter(EmployeePreviewParameter::class)
    employeeState: UiState<Employee>,
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        EmployeeDetails(
            employeeState = employeeState,
            modifier = modifier,
            employeeDetailsExpanded = true,
            onClickEdit = {},
            onExpanded = {},
        )
    }
}
