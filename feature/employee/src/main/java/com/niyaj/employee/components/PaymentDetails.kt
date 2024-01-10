package com.niyaj.employee.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toFormattedDate
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.EmployeePayments
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.event.UiState

/**
 *
 */
@Composable
fun PaymentDetails(
    employeePaymentsState: UiState<List<EmployeePayments>>,
    paymentDetailsExpanded: Boolean = false,
    onExpanded: () -> Unit = {},
) = trace("PaymentDetails") {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onExpanded()
            }
            .testTag("PaymentDetails"),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 1.dp,
        )
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
                    icon = Icons.Default.Money
                )
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = {
                        onExpanded()
                    }
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
                    targetState = employeePaymentsState,
                    label = "PaymentDetails"
                ) { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicator()

                        UiState.Empty -> {
                            ItemNotAvailable(
                                text = "You have not paid any amount to this employee.",
                                showImage = false,
                            )
                        }

                        is UiState.Success -> {
                            Spacer(modifier = Modifier.height(SpaceSmall))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                state.data.forEachIndexed { index, salaries ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(SpaceSmall),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Column {
                                            Text(
                                                text = "${salaries.startDate.toFormattedDate} - ${salaries.endDate.toFormattedDate}",
                                                fontWeight = FontWeight.Bold,
                                            )
                                            Spacer(modifier = Modifier.height(SpaceSmall))
                                            Text(
                                                text = salaries.payments.sumOf { it.paymentAmount.toLong() }
                                                    .toString().toRupee,
                                                fontWeight = FontWeight.SemiBold,
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(SpaceSmall))
                                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                                        Spacer(modifier = Modifier.height(SpaceSmall))

                                        salaries.payments.forEachIndexed { index, salary ->
                                            EmployeePayment(payment = salary)

                                            if (index != salaries.payments.size - 1) {
                                                Spacer(modifier = Modifier.height(SpaceSmall))
                                                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                                                Spacer(modifier = Modifier.height(SpaceSmall))
                                            }
                                        }
                                    }

                                    if (index != state.data.size - 1) {
                                        Spacer(modifier = Modifier.height(SpaceMini))
                                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                                        Spacer(modifier = Modifier.height(SpaceMini))
                                    }
                                }
                            }
                        }
                    }
                }
            },
        )
    }
}
