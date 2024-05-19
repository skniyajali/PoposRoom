package com.niyaj.feature.reports.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.LightColor3
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.chart.common.dimens.ChartDimens
import com.niyaj.feature.chart.horizontalbar.HorizontalBarChart
import com.niyaj.feature.chart.horizontalbar.axis.HorizontalAxisConfig
import com.niyaj.feature.chart.horizontalbar.config.HorizontalBarConfig
import com.niyaj.feature.chart.horizontalbar.config.StartDirection
import com.niyaj.feature.chart.horizontalbar.model.HorizontalBarData
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.event.UiState

@Composable
fun ProductWiseReport(
    productState: UiState<List<HorizontalBarData>>,
    orderType: String,
    productRepExpanded: Boolean,
    selectedProduct: String,
    onExpandChanged: () -> Unit,
    onClickOrderType: (String) -> Unit,
    onBarClick: (String) -> Unit,
) = trace("ProductWiseReport") {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightColor3,
        )
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = productRepExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                IconWithText(
                    text = "Product Wise Report",
                    secondaryText = selectedProduct.ifEmpty { null },
                    icon = PoposIcons.Dns,
                )
            },
            trailing = {
                OrderTypeDropdown(
                    text = orderType.ifEmpty { "All" },
                    onItemClick = onClickOrderType
                )
            },
            expand = null,
            contentDesc = "Product wise report",
            content = {
                Crossfade(
                    targetState = productState,
                    label = "ProductState"
                ) { state ->
                    when(state) {
                        is UiState.Empty -> {
                            ItemNotAvailable(
                                text = "Product wise report not available",
                                showImage = false,
                            )
                        }

                        is UiState.Loading -> LoadingIndicator()

                        is UiState.Success -> {
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            HorizontalBarChart(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((state.data.size.times(50)).dp)
                                    .padding(SpaceSmall),
                                onBarClick = {
                                    onBarClick(
                                        "${it.yValue} - ${
                                            it.xValue.toString().substringBefore(".")
                                        } Qty"
                                    )
                                },
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.secondaryContainer
                                ),
                                barDimens = ChartDimens(2.dp),
                                horizontalBarConfig = HorizontalBarConfig(
                                    showLabels = false,
                                    startDirection = StartDirection.Left
                                ),
                                horizontalAxisConfig = HorizontalAxisConfig(
                                    showAxes = true,
                                    showUnitLabels = false
                                ),
                                horizontalBarData = state.data,
                            )
                        }
                    }
                }
            },
        )
    }
}