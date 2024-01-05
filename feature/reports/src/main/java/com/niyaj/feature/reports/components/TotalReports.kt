package com.niyaj.feature.reports.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.trace
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.model.Reports
import com.niyaj.ui.components.ReportBox
import com.niyaj.ui.components.StandardButton

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TotalReports(
    report: Reports,
    onOrderClick: () -> Unit,
    onExpensesClick: () -> Unit,
    onRefreshReport: () -> Unit,
) = trace("ReportBoxData") {
    val totalAmount =
        report.expensesAmount.plus(report.dineInSalesAmount).plus(report.dineOutSalesAmount)
            .toString()

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        maxItemsInEachRow = 2,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(SpaceMedium, Alignment.CenterVertically),
    ) {
        ReportBox(
            title = "DineIn Sales",
            amount = report.dineInSalesAmount.toString(),
            icon = Icons.Default.RamenDining,
            onClick = onOrderClick,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )

        ReportBox(
            title = "DineOut Sales",
            amount = report.dineOutSalesAmount.toString(),
            icon = Icons.Default.DeliveryDining,
            onClick = onOrderClick,
            containerColor = MaterialTheme.colorScheme.errorContainer
        )

        ReportBox(
            title = "Expenses",
            amount = report.expensesAmount.toString(),
            icon = Icons.Default.Receipt,
            onClick = onExpensesClick,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )

        ReportBox(
            title = "Total Amount",
            amount = totalAmount,
            icon = Icons.Default.Money,
            enabled = false,
            onClick = {},
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    }

    Spacer(modifier = Modifier.height(SpaceMedium))

    StandardButton(
        modifier = Modifier.fillMaxWidth(),
        text = "Re-Generate Report",
        icon = Icons.Default.Sync,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
        onClick = onRefreshReport
    )
}