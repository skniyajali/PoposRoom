package com.niyaj.feature.reports.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.reports.CustomerWiseReportState
import com.niyaj.ui.components.CountBox
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardExpandable

@Composable
fun CustomerWiseReport(
    customerState: CustomerWiseReportState,
    customerWiseRepExpanded: Boolean,
    onExpandChanged: () -> Unit,
    onCustomerClick: (Int) -> Unit,
) = trace("CustomerWiseReport") {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors()
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
                    icon = Icons.Default.PeopleAlt,
                )
            },
            trailing = {
                CountBox(count = customerState.reports.size.toString())
            },
            rowClickable = true,
            expand = null,
            content = {
                Crossfade(targetState = customerState, label = "CustomerState") { state ->
                    when {
                        state.isLoading -> LoadingIndicator()
                        state.reports.isNotEmpty() -> {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                state.reports.forEachIndexed { index, report ->
                                    CustomerReportCard(
                                        customerReport = report,
                                        onClickCustomer = onCustomerClick
                                    )

                                    if (index != state.reports.size - 1) {
                                        Spacer(modifier = Modifier.height(SpaceMini))
                                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                                        Spacer(modifier = Modifier.height(SpaceMini))
                                    }
                                }
                            }
                        }

                        else -> {
                            ItemNotAvailable(
                                text = "Customer wise report not available",
                                showImage = false
                            )
                        }
                    }
                }
            },
            contentDesc = "Customer wise report"
        )
    }
}