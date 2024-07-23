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

package com.niyaj.feature.reports.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.components.PoposIconButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.CustomerWiseReport
import com.niyaj.model.TotalOrders
import com.niyaj.ui.components.CountBox
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailableHalf
import com.niyaj.ui.components.LoadingIndicatorHalf
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.CustomerWiseReportPreviewParameter
import com.niyaj.ui.utils.DevicePreviews

@Composable
internal fun CustomerWiseReport(
    customerState: UiState<List<CustomerWiseReport>>,
    totalReports: TotalOrders,
    customerWiseRepExpanded: Boolean,
    onExpandChanged: () -> Unit,
    onCustomerClick: (Int) -> Unit,
    onPrintCustomerWiseReport: () -> Unit,
) = trace("CustomerWiseReport") {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.elevatedCardColors(),
    ) {
        StandardExpandable(
            modifier = Modifier
                .padding(SpaceSmall)
                .fillMaxWidth(),
            expanded = customerWiseRepExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                IconWithText(
                    text = "Customer Wise Report",
                    icon = PoposIcons.PeopleAlt,
                )
            },
            trailing = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SpaceMini),
                ) {
                    CountBox(count = totalReports.totalAmount.toRupee)

                    CountBox(
                        count = totalReports.totalOrders.toString(),
                        backgroundColor = MaterialTheme.colorScheme.secondary,
                    )

                    PoposIconButton(
                        icon = PoposIcons.Print,
                        onClick = onPrintCustomerWiseReport,
                    )
                }
            },
            rowClickable = true,
            expand = null,
            content = {
                Crossfade(
                    targetState = customerState,
                    label = "CustomerState",
                ) { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicatorHalf()

                        is UiState.Empty -> {
                            ItemNotAvailableHalf(
                                text = "Customer wise report not available",
                                showImage = false,
                            )
                        }

                        is UiState.Success -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                state.data.forEachIndexed { index, report ->
                                    CustomerReportCard(
                                        customerReport = report,
                                        onClickCustomer = onCustomerClick,
                                    )

                                    if (index != state.data.size - 1) {
                                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                                    }
                                }
                            }
                        }
                    }
                }
            },
            contentDesc = "Customer wise report",
        )
    }
}

@Composable
private fun CustomerReportCard(
    modifier: Modifier = Modifier,
    customerReport: CustomerWiseReport,
    onClickCustomer: (Int) -> Unit,
) = trace("CustomerReportCard") {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClickCustomer(customerReport.customerId) }
            .padding(SpaceSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start,
        ) {
            customerReport.customerName?.let {
                IconWithText(
                    text = it,
                    icon = PoposIcons.Person,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(SpaceMini))
            }

            IconWithText(
                text = customerReport.customerPhone,
                icon = PoposIcons.PhoneAndroid,
                fontWeight = FontWeight.SemiBold,
            )

            customerReport.customerEmail?.let { email ->
                Spacer(modifier = Modifier.height(SpaceMini))
                IconWithText(
                    text = email,
                    icon = PoposIcons.Email,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Spacer(modifier = Modifier.height(SpaceMini))

        Text(
            text = customerReport.totalSales.toRupee,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.End,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(SpaceMini))

        Text(
            text = customerReport.totalOrders.toString(),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}

@DevicePreviews
@Composable
private fun CustomerWiseReportPreview(
    @PreviewParameter(CustomerWiseReportPreviewParameter::class)
    customerState: UiState<List<CustomerWiseReport>>,
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        CustomerWiseReport(
            customerState = customerState,
            totalReports = TotalOrders(
                totalAmount = 5000L,
                totalOrders = 5,
            ),
            customerWiseRepExpanded = true,
            onExpandChanged = {},
            onCustomerClick = {},
            onPrintCustomerWiseReport = {},
        )
    }
}
