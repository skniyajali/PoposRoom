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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toBarDate
import com.niyaj.common.utils.toFormattedDate
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.EmployeePayments
import com.niyaj.model.Payment
import com.niyaj.model.PaymentMode
import com.niyaj.ui.components.IconBox
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailableHalf
import com.niyaj.ui.components.LoadingIndicatorHalf
import com.niyaj.ui.components.NoteText
import com.niyaj.ui.components.PoposOutlinedChip
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.EmployeePaymentsPreviewParameter
import com.niyaj.ui.parameterProvider.EmployeePreviewData
import com.niyaj.ui.utils.DevicePreviews

/**
 *
 */
@Composable
internal fun PaymentDetails(
    modifier: Modifier = Modifier,
    employeePaymentsState: UiState<List<EmployeePayments>>,
    paymentDetailsExpanded: Boolean = false,
    onExpanded: () -> Unit = {},
) = trace("PaymentDetails") {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onExpanded()
            }
            .testTag("PaymentDetails"),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 1.dp,
        ),
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = paymentDetailsExpanded,
            onExpandChanged = {
                onExpanded()
            },
            title = {
                IconWithText(
                    text = "Payment Details",
                    icon = PoposIcons.Money,
                )
            },
            rowClickable = false,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = {
                        onExpanded()
                    },
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
                    targetState = employeePaymentsState,
                    label = "PaymentDetails",
                ) { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicatorHalf()

                        is UiState.Empty -> {
                            ItemNotAvailableHalf(
                                text = "You have not paid any amount to this employee.",
                                showImage = false,
                            )
                        }

                        is UiState.Success -> {
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            EmployeePaymentsList(
                                modifier = Modifier.fillMaxWidth(),
                                paymentList = state.data,
                            )
                        }
                    }
                }
            },
        )
    }
}

@Composable
private fun EmployeePaymentsList(
    modifier: Modifier = Modifier,
    paymentList: List<EmployeePayments>
) {
    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = SpaceMini),
        ) {
            paymentList.forEachIndexed { index, salaries ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = SpaceMini, horizontal = SpaceSmall),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconWithText(
                            text = "${salaries.startDate.toFormattedDate} - ${salaries.endDate.toFormattedDate}",
                            icon = PoposIcons.CalenderMonth,
                            fontWeight = FontWeight.Bold,
                            tintColor = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.height(SpaceSmall))
                        Text(
                            text = salaries.payments.sumOf { it.paymentAmount.toLong() }
                                .toString().toRupee,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceMini))
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(SpaceMini))

                    salaries.payments.forEachIndexed { index, salary ->
                        EmployeePayment(
                            modifier = Modifier.padding(horizontal = SpaceSmall),
                            payment = salary
                        )

                        if (index != salaries.payments.size - 1) {
                            Spacer(modifier = Modifier.height(SpaceMini))
                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(SpaceMini))
                        }
                    }

                    if (salaries.payments.isEmpty()) {
                        NoteText(
                            text = "Payments were not made on this date period!",
                            modifier = Modifier
                                .padding(vertical = SpaceMini)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }

                if (index != paymentList.size - 1) {
                    Spacer(modifier = Modifier.height(SpaceMini))
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(SpaceMini))
                }
            }
        }
    }
}

@Composable
private fun EmployeePayment(
    modifier: Modifier = Modifier,
    payment: Payment,
) = trace("EmployeePayment") {
    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .testTag("Payment Tag")
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = payment.paymentAmount.toRupee,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(0.8F),
            )

            Text(
                text = payment.paymentDate.toBarDate,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(0.8F),
            )

            Row(
                modifier = Modifier.weight(1.4F),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                IconBox(
                    text = payment.paymentMode.name,
                    icon = when (payment.paymentMode) {
                        PaymentMode.Cash -> PoposIcons.Money
                        PaymentMode.Online -> PoposIcons.AccountBalance
                        else -> PoposIcons.Payments
                    },
                    selected = false,
                )

                Spacer(modifier = Modifier.width(SpaceSmall))

                PoposOutlinedChip(text = payment.paymentType.name)
            }
        }
    }
}

@DevicePreviews
@Composable
private fun PaymentDetailsPreview(
    @PreviewParameter(EmployeePaymentsPreviewParameter::class)
    employeePaymentsState: UiState<List<EmployeePayments>>,
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        PaymentDetails(
            modifier = modifier,
            employeePaymentsState = employeePaymentsState,
            paymentDetailsExpanded = true,
            onExpanded = {},
        )
    }
}

@DevicePreviews
@Composable
private fun EmployeePaymentPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        Surface {
            EmployeePayment(
                modifier = modifier,
                payment = EmployeePreviewData.samplePayment,
            )
        }
    }
}