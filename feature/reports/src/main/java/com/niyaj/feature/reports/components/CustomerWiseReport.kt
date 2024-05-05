package com.niyaj.feature.reports.components

import androidx.compose.animation.Crossfade
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.CustomerWiseReport
import com.niyaj.model.TotalOrders
import com.niyaj.ui.components.CountBox
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.event.UiState

@Composable
fun CustomerWiseReport(
    customerState: UiState<List<CustomerWiseReport>>,
    totalReports: TotalOrders,
    customerWiseRepExpanded: Boolean,
    onExpandChanged: () -> Unit,
    onCustomerClick: (Int) -> Unit,
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
                        is UiState.Loading -> LoadingIndicator()

                        is UiState.Empty -> {
                            ItemNotAvailable(
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