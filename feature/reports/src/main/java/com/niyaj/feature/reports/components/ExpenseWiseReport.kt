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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_TAG
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.components.PoposIconButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.ExpensesReport
import com.niyaj.model.TotalExpenses
import com.niyaj.ui.components.CountBox
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailableHalf
import com.niyaj.ui.components.LoadingIndicatorHalf
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.ExpensesReportPreviewParameter
import com.niyaj.ui.utils.DevicePreviews

@Composable
internal fun ExpenseWiseReport(
    modifier: Modifier = Modifier,
    uiState: UiState<List<ExpensesReport>>,
    totalReports: TotalExpenses,
    doesExpanded: Boolean,
    onExpandChanged: () -> Unit,
    onPrintExpenseWiseReport: () -> Unit,
    onExpenseClick: (Int) -> Unit = {},
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.elevatedCardColors(),
    ) {
        StandardExpandable(
            modifier = modifier
                .padding(SpaceSmall)
                .fillMaxWidth(),
            expanded = doesExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                IconWithText(
                    text = "Expenses Report",
                    icon = PoposIcons.Receipt,
                )
            },
            trailing = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(SpaceMini),
                ) {
                    CountBox(count = totalReports.totalExpenses.toRupee)

                    CountBox(
                        count = totalReports.totalQuantity.toString(),
                        backgroundColor = MaterialTheme.colorScheme.secondary,
                    )

                    PoposIconButton(
                        icon = PoposIcons.Print,
                        onClick = onPrintExpenseWiseReport,
                    )
                }
            },
            rowClickable = true,
            expand = null,
            content = {
                when (uiState) {
                    is UiState.Loading -> LoadingIndicatorHalf()

                    is UiState.Empty -> {
                        ItemNotAvailableHalf(
                            text = "Expenses not available on selected date.",
                            showImage = false,
                        )
                    }

                    is UiState.Success -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            uiState.data.forEachIndexed { index, report ->
                                ExpensesReportCard(
                                    report = report,
                                    onExpenseClick = onExpenseClick,
                                )

                                // Add Spacer until the last item
                                if (index != uiState.data.size - 1) {
                                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                                }
                            }
                        }
                    }
                }
            },
        )
    }
}

@Composable
private fun ExpensesReportCard(
    modifier: Modifier = Modifier,
    report: ExpensesReport,
    onClickEnable: Boolean = false,
    onExpenseClick: (Int) -> Unit = {},
) = trace("ExpensesReportCard") {
    Row(
        modifier = modifier
            .testTag(EXPENSE_TAG.plus(report.expenseId))
            .fillMaxWidth()
            .clickable(onClickEnable) { onExpenseClick(report.expenseId) }
            .padding(SpaceSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconWithText(
            text = report.expenseName,
            icon = PoposIcons.Person,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )

        Text(
            text = report.expenseAmount.toRupee,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}

@DevicePreviews
@Composable
private fun ExpenseWiseReportPreview(
    @PreviewParameter(ExpensesReportPreviewParameter::class)
    uiState: UiState<List<ExpensesReport>>,
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        ExpenseWiseReport(
            modifier = modifier,
            uiState = uiState,
            totalReports = TotalExpenses(
                totalExpenses = 5000,
                totalQuantity = 5,
            ),
            doesExpanded = true,
            onExpandChanged = {},
            onPrintExpenseWiseReport = {},
        )
    }
}
