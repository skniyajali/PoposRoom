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

package com.niyaj.employeePayment.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_TAG
import com.niyaj.common.utils.toBarDate
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeWithPayments
import com.niyaj.model.Payment
import com.niyaj.model.PaymentMode
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.PoposChip
import com.niyaj.ui.components.PoposOutlinedChip
import com.niyaj.ui.parameterProvider.PaymentPreviewData
import com.niyaj.ui.utils.DevicePreviews
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun PaymentsList(
    modifier: Modifier = Modifier,
    viewType: ViewType,
    employee: Employee,
    payments: ImmutableList<Payment>,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    showTrailingIcon: Boolean,
    showEmployeeDetails: Boolean,
    onClickAddPayment: (employeeId: Int) -> Unit,
    onClickEmployee: (employeeId: Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    containerColor: Color = MaterialTheme.colorScheme.background,
    listColor: Color = MaterialTheme.colorScheme.primaryContainer,
    trailingIconColor: Color = MaterialTheme.colorScheme.secondary,
) = trace("PaymentData") {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors().copy(
            containerColor = containerColor,
        ),
    ) {
        ListItem(
            colors = ListItemDefaults.colors(
                containerColor = listColor,
            ),
            modifier = modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = showEmployeeDetails,
                ) {
                    onClickEmployee(employee.employeeId)
                },
            leadingContent = {
                CircularBox(
                    icon = PoposIcons.Money,
                    doesSelected = false,
                    text = employee.employeeName,
                )
            },
            headlineContent = {
                Text(
                    text = employee.employeeName,
                    style = MaterialTheme.typography.labelLarge,
                )
            },
            supportingContent = {
                Text(text = employee.employeePhone)
            },
            trailingContent = if (showTrailingIcon) {
                {
                    FilledTonalIconButton(
                        onClick = {
                            onClickAddPayment(employee.employeeId)
                        },
                        shape = RoundedCornerShape(SpaceMini),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = trailingIconColor,
                        ),
                    ) {
                        Icon(
                            imageVector = PoposIcons.Add,
                            contentDescription = "Add New Payment",
                        )
                    }
                }
            } else {
                null
            },
        )

        payments.forEachIndexed { index, salary ->
            Crossfade(
                targetState = viewType,
                label = "ViewType",
            ) {
                when (it) {
                    ViewType.LIST -> {
                        PaymentListView(
                            modifier = Modifier,
                            payment = salary,
                            doesSelected = doesSelected,
                            onClick = onClick,
                            onLongClick = onLongClick,
                            border = border,
                        )
                    }

                    ViewType.CARD -> {
                        PaymentCardView(
                            modifier = Modifier,
                            payment = salary,
                            doesSelected = doesSelected,
                            onClick = onClick,
                            onLongClick = onLongClick,
                        )
                    }
                }
            }

            if (index != payments.size - 1) {
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PaymentListView(
    modifier: Modifier = Modifier,
    payment: Payment,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
) = trace("EmployeePayment") {
    val borderStroke = if (doesSelected(payment.paymentId)) border else null

    Box(
        modifier = modifier
            .fillMaxWidth()
            .testTag(PAYMENT_TAG.plus(payment.paymentId))
            .then(
                borderStroke?.let {
                    Modifier.border(it)
                } ?: Modifier,
            )
            .combinedClickable(
                onClick = {
                    onClick(payment.paymentId)
                },
                onLongClick = {
                    onLongClick(payment.paymentId)
                },
            ),
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = SpaceMedium, vertical = SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = payment.paymentAmount.toRupee,
                style = MaterialTheme.typography.labelLarge,
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
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                PoposChip(
                    text = payment.paymentMode.name,
                    icon = when (payment.paymentMode) {
                        PaymentMode.Cash -> PoposIcons.Money
                        PaymentMode.Online -> PoposIcons.AccountBalance
                        else -> PoposIcons.Payments
                    },
                )

                Spacer(modifier = Modifier.width(SpaceSmall))

                PoposOutlinedChip(
                    text = payment.paymentType.name,
                    icon = PoposIcons.MergeType,
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PaymentCardView(
    modifier: Modifier = Modifier,
    payment: Payment,
    doesSelected: (Int) -> Boolean,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.background,
) = trace("EmployeePaymentCardView") {
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = containerColor,
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag(PAYMENT_TAG.plus(payment.paymentId))
            .combinedClickable(
                onClick = {
                    onClick(payment.paymentId)
                },
                onLongClick = {
                    onLongClick(payment.paymentId)
                },
            ),
        leadingContent = {
            CircularBox(
                icon = when (payment.paymentMode) {
                    PaymentMode.Cash -> PoposIcons.Money
                    PaymentMode.Online -> PoposIcons.AccountBalance
                    else -> PoposIcons.Payments
                },
                doesSelected = doesSelected(payment.paymentId),
            )
        },
        headlineContent = {
            Text(
                text = payment.paymentAmount.toRupee,
                style = MaterialTheme.typography.labelLarge,
            )
        },
        supportingContent = {
            Text(text = payment.paymentDate.toBarDate)
        },
        trailingContent = {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                PoposChip(
                    text = payment.paymentMode.name,
                    icon = when (payment.paymentMode) {
                        PaymentMode.Cash -> PoposIcons.Money
                        PaymentMode.Online -> PoposIcons.AccountBalance
                        else -> PoposIcons.Payments
                    },
                )

                Spacer(modifier = Modifier.width(SpaceSmall))

                PoposOutlinedChip(
                    text = payment.paymentType.name,
                    icon = PoposIcons.MergeType,
                )
            }
        },
    )
}

@DevicePreviews
@Composable
private fun PaymentsListViewPreview(
    modifier: Modifier = Modifier,
    item: EmployeeWithPayments = PaymentPreviewData.employeesWithPayments.first(),
) {
    PoposRoomTheme {
        PaymentsList(
            modifier = modifier,
            viewType = ViewType.LIST,
            employee = item.employee,
            payments = item.payments.toImmutableList(),
            doesSelected = { false },
            onClick = {},
            onLongClick = {},
            showTrailingIcon = false,
            showEmployeeDetails = false,
            onClickAddPayment = {},
            onClickEmployee = {},
        )
    }
}

@DevicePreviews
@Composable
private fun PaymentsListCardViewPreview(
    modifier: Modifier = Modifier,
    item: EmployeeWithPayments = PaymentPreviewData.employeesWithPayments.first(),
) {
    PoposRoomTheme {
        PaymentsList(
            modifier = modifier,
            viewType = ViewType.CARD,
            employee = item.employee,
            payments = item.payments.toImmutableList(),
            doesSelected = { false },
            onClick = {},
            onLongClick = {},
            showTrailingIcon = true,
            showEmployeeDetails = false,
            onClickAddPayment = {},
            onClickEmployee = {},
        )
    }
}
