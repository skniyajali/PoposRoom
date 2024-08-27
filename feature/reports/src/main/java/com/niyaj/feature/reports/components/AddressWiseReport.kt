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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
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
import com.niyaj.model.AddressWiseReport
import com.niyaj.model.TotalOrders
import com.niyaj.ui.components.CountBox
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailableHalf
import com.niyaj.ui.components.LoadingIndicatorHalf
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.AddressWiseReportPreviewParameter
import com.niyaj.ui.utils.DevicePreviews

@Composable
internal fun AddressWiseReport(
    addressState: UiState<List<AddressWiseReport>>,
    totalReports: TotalOrders,
    addressWiseRepExpanded: Boolean,
    onExpandChanged: () -> Unit,
    onAddressClick: (addressId: Int) -> Unit,
    onPrintAddressWiseReport: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.background,
) = trace("AddressWiseReport") {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.elevatedCardColors().copy(
            containerColor = containerColor,
        ),
    ) {
        StandardExpandable(
            expanded = addressWiseRepExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            content = {
                Crossfade(targetState = addressState, label = "AddressState") { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicatorHalf()

                        is UiState.Empty -> {
                            ItemNotAvailableHalf(
                                text = "Address wise report not available",
                                showImage = false,
                            )
                        }

                        is UiState.Success -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                state.data.forEachIndexed { index, report ->
                                    AddressReportCard(
                                        report = report,
                                        onAddressClick = onAddressClick,
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
            modifier = Modifier
                .padding(SpaceSmall)
                .fillMaxWidth(),
            rowClickable = true,
            showExpandIcon = false,
            title = {
                IconWithText(
                    text = "Address Wise Report",
                    icon = PoposIcons.Address,
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
                        onClick = onPrintAddressWiseReport,
                    )
                }
            },
            expand = null,
            contentDesc = "Address wise report",
        )
    }
}

@Composable
private fun AddressReportCard(
    report: AddressWiseReport,
    onAddressClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) = trace("AddressReportCard") {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onAddressClick(report.addressId) },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconWithText(
                text = report.addressName,
                icon = PoposIcons.LocationOn,
                modifier = Modifier.weight(1F),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                text = report.shortName,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(0.5F),
            )

            Text(
                text = report.totalSales.toRupee,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.5F),
            )

            Text(
                text = report.totalOrders.toString(),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(0.5F),
            )
        }
    }
}

@DevicePreviews
@Composable
private fun AddressWiseReportPreview(
    @PreviewParameter(AddressWiseReportPreviewParameter::class)
    addressState: UiState<List<AddressWiseReport>>,
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        AddressWiseReport(
            addressState = addressState,
            totalReports = TotalOrders(
                totalAmount = 3000L,
                totalOrders = 5,
            ),
            modifier = modifier,
            addressWiseRepExpanded = true,
            onExpandChanged = {},
            onAddressClick = {},
            onPrintAddressWiseReport = {},
        )
    }
}
