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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.EmployeeTestTags
import com.niyaj.common.utils.Constants
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.EmployeeMonthlyDate
import com.niyaj.model.EmployeeSalaryEstimation
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.PaymentStatusChip
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.ui.event.UiState

/**
 *
 */
@Composable
fun SalaryEstimationCard(
    uiState: UiState<EmployeeSalaryEstimation>,
    dropdownText: String = "",
    salaryDates: List<EmployeeMonthlyDate> = emptyList(),
    onDateClick: (Pair<String, String>) -> Unit = {},
    onClickPaymentCount: () -> Unit = {},
    onClickAbsentCount: () -> Unit = {},
    onClickAbsentEntry: () -> Unit = {},
    onClickSalaryEntry: () -> Unit = {},
) = trace("SalaryEstimationCard") {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Salary Estimation",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            SalaryDateDropdown(
                text = dropdownText,
                salaryDates = salaryDates,
                onDateClick = {
                    onDateClick(it)
                },
            )
        }

        Spacer(modifier = Modifier.height(SpaceSmall))
        Spacer(modifier = Modifier.height(SpaceSmall))
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceSmall))

        Crossfade(
            targetState = uiState,
            label = "SalaryEstimationState",
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    Text(
                        text = "Something went wrong!",
                        textAlign = TextAlign.Center,
                    )
                }

                is UiState.Success -> {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = state.data.remainingAmount.toRupee,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.testTag(EmployeeTestTags.REMAINING_AMOUNT_TEXT),
                            )

                            Column(
                                horizontalAlignment = Alignment.End,
                            ) {
                                PaymentStatusChip(isPaid = state.data.status == Constants.PAID)

                                state.data.message?.let {
                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                    Text(
                                        text = it,
                                        color = if (state.data.status == Constants.PAID) {
                                            MaterialTheme.colorScheme.error
                                        } else {
                                            MaterialTheme.colorScheme.primary
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        textAlign = TextAlign.End,
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(SpaceSmall))
                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            SuggestionChip(
                                modifier = Modifier.testTag("AdvancePayment"),
                                onClick = onClickPaymentCount,
                                label = {
                                    Text(text = "${state.data.paymentCount} Advance Payment")
                                },
                            )

                            Spacer(modifier = Modifier.width(SpaceSmall))

                            SuggestionChip(
                                modifier = Modifier.testTag("DaysAbsent"),
                                onClick = onClickAbsentCount,
                                label = {
                                    Text(text = "${state.data.absentCount} Days Absent")
                                },
                            )
                        }

                        Spacer(modifier = Modifier.height(SpaceSmall))
                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(SpaceSmall))
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        PoposButton(
                            text = "Add Absent Entry",
                            icon = PoposIcons.EventBusy,
                            onClick = onClickAbsentEntry,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.error,
                            ),
                        )

                        Spacer(modifier = Modifier.height(SpaceSmall))

                        PoposButton(
                            text = "Add Payment Entry",
                            icon = PoposIcons.Money,
                            onClick = onClickSalaryEntry,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                            ),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(SpaceSmall))
    }
}
