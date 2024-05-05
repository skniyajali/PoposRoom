package com.niyaj.feature.reports.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.trace
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.model.Reports
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.ReportBox
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.event.UiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TotalReports(
    modifier: Modifier = Modifier,
    uiState: UiState<Reports>,
    onOrderClick: () -> Unit,
    onExpensesClick: () -> Unit,
    onRegenerateReport: () -> Unit,
) = trace("ReportBoxData") {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpaceMedium, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Crossfade(
            targetState = uiState,
            label = "TotalReports::State",
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {}

                is UiState.Success -> {
                    val report = state.data

                    val totalAmount =
                        report.expensesAmount.plus(report.dineInSalesAmount)
                            .plus(report.dineOutSalesAmount)
                            .toString()

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        maxItemsInEachRow = 2,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalArrangement = Arrangement.spacedBy(
                            SpaceMedium,
                            Alignment.CenterVertically,
                        ),
                    ) {
                        ReportBox(
                            title = "DineIn Sales",
                            amount = report.dineInSalesAmount.toString(),
                            icon = PoposIcons.DinnerDining,
                            onClick = onOrderClick,
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        )

                        ReportBox(
                            title = "DineOut Sales",
                            amount = report.dineOutSalesAmount.toString(),
                            icon = PoposIcons.DeliveryDining,
                            onClick = onOrderClick,
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                        )

                        ReportBox(
                            title = "Expenses",
                            amount = report.expensesAmount.toString(),
                            icon = PoposIcons.Receipt,
                            onClick = onExpensesClick,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                        )

                        ReportBox(
                            title = "Total Amount",
                            amount = totalAmount,
                            icon = PoposIcons.Money,
                            enabled = false,
                            onClick = {},
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        )
                    }
                }
            }
        }

        StandardButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Re-Generate Report",
            icon = PoposIcons.Sync,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            onClick = onRegenerateReport,
        )
    }
}