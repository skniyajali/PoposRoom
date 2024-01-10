package com.niyaj.employee.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.MergeType
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Merge
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toDate
import com.niyaj.common.utils.toJoinedDate
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Employee
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.event.UiState


/**
 *
 */
@Composable
fun EmployeeDetails(
    employeeState: UiState<Employee>,
    employeeDetailsExpanded: Boolean = false,
    onClickEdit: () -> Unit = {},
    onExpanded: () -> Unit = {},
)= trace("EmployeeDetails") {
    Card(
        onClick = onExpanded,
        modifier = Modifier
            .testTag("EmployeeDetails")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 1.dp,
        )
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
                    icon = Icons.Default.Person
                )
            },
            trailing = {
                IconButton(
                    onClick = onClickEdit
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Employee",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = onExpanded
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            content = {
                Crossfade(
                    targetState = employeeState,
                    label = "EmployeeDetailsState"
                ) { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicator()

                        is UiState.Empty -> {
                            ItemNotAvailable(
                                text = "Employee details not found",
                                showImage = false,
                            )
                        }

                        is UiState.Success -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SpaceSmall)
                            ) {
                                IconWithText(
                                    modifier = Modifier.testTag(state.data.employeeName),
                                    text = "Name - ${state.data.employeeName}",
                                    icon = Icons.Default.Person
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                IconWithText(
                                    modifier = Modifier.testTag(state.data.employeePhone),
                                    text = "Phone - ${state.data.employeePhone}",
                                    icon = Icons.Default.PhoneAndroid
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                IconWithText(
                                    modifier = Modifier.testTag(state.data.employeeSalary.toRupee),
                                    text = "Salary - ${state.data.employeeSalary.toRupee}",
                                    icon = Icons.Default.CurrencyRupee
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                IconWithText(
                                    modifier = Modifier.testTag(state.data.employeeSalaryType.name),
                                    text = "Salary Type - ${state.data.employeeSalaryType}",
                                    icon = Icons.Default.Merge
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                IconWithText(
                                    modifier = Modifier.testTag(state.data.employeePosition),
                                    text = "Position - ${state.data.employeePosition}",
                                    icon = Icons.Default.Approval
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                IconWithText(
                                    modifier = Modifier.testTag(state.data.employeeType.name),
                                    text = "Type - ${state.data.employeeType}",
                                    icon = Icons.AutoMirrored.Filled.MergeType
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                IconWithText(
                                    modifier = Modifier.testTag(state.data.employeeJoinedDate.toDate),
                                    text = "Joined Date : ${state.data.employeeJoinedDate.toJoinedDate}",
                                    icon = Icons.Default.CalendarToday
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                IconWithText(
                                    text = "Created At : ${state.data.createdAt.toPrettyDate()}",
                                    icon = Icons.Default.AccessTime
                                )
                                state.data.updatedAt?.let {
                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                    IconWithText(
                                        text = "Updated At : ${it.toPrettyDate()}",
                                        icon = Icons.AutoMirrored.Filled.Login
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
