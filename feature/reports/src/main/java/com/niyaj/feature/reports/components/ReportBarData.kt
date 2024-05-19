package com.niyaj.feature.reports.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
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
import com.niyaj.ui.event.UiState

@Composable
fun ReportBarData(
    reportBarState: UiState<List<HorizontalBarData>>,
    selectedBarData: String,
    onBarClick: (String) -> Unit,
    onClickViewMore: () -> Unit,
) = trace("ReportBarData") {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors()
    ) {
        Crossfade(
            targetState = reportBarState,
            label = "ReportBarState"
        ) { state ->
            when(state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        modifier = Modifier.padding(SpaceSmall),
                        text = "Reports are not available",
                        showImage = false,
                    )
                }

                is UiState.Success -> {
                    Column(
                        modifier = Modifier
                            .padding(SpaceSmall)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            IconWithText(
                                text = "Last ${state.data.size} Days Reports",
                                icon = PoposIcons.AutoGraph,
                                secondaryText = selectedBarData.ifEmpty { null }
                            )

                            IconButton(
                                onClick = onClickViewMore
                            ) {
                                Icon(
                                    imageVector = PoposIcons.ArrowRightAlt,
                                    contentDescription = "View more"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(SpaceSmall))

                        HorizontalDivider(modifier = Modifier.fillMaxWidth())

                        Spacer(modifier = Modifier.height(SpaceSmall))

                        HorizontalBarChart(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((state.data.size.times(60)).dp)
                                .padding(SpaceSmall),
                            onBarClick = {
                                onBarClick(
                                    "${it.yValue} - ${
                                        it.xValue.toString().substringBefore(".").toRupee
                                    }"
                                )
                            },
                            colors = listOf(
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.primaryContainer
                            ),
                            barDimens = ChartDimens(2.dp),
                            horizontalBarConfig = HorizontalBarConfig(
                                showLabels = false,
                                startDirection = StartDirection.Left,
                                productReport = false
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
        }
    }
}