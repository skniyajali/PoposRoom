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

package com.niyaj.feature.chart.bar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import com.niyaj.feature.chart.bar.common.calculations.getTopLeft
import com.niyaj.feature.chart.bar.common.calculations.getTopRight
import com.niyaj.feature.chart.bar.common.component.drawBarLabel
import com.niyaj.feature.chart.bar.config.BarConfig
import com.niyaj.feature.chart.bar.config.BarConfigDefaults
import com.niyaj.feature.chart.bar.model.BarData
import com.niyaj.feature.chart.bar.model.GroupedBarData
import com.niyaj.feature.chart.bar.model.maxYValue
import com.niyaj.feature.chart.bar.model.totalItems
import com.niyaj.feature.chart.common.axis.AxisConfig
import com.niyaj.feature.chart.common.axis.AxisConfigDefaults
import com.niyaj.feature.chart.common.axis.drawYAxisWithLabels
import com.niyaj.feature.chart.common.dimens.ChartDimens
import com.niyaj.feature.chart.common.dimens.ChartDimensDefaults

@Composable
fun GroupedBarChart(
    groupedBarData: List<GroupedBarData>,
    modifier: Modifier = Modifier,
    onBarClick: (BarData) -> Unit = {},
    barDimens: ChartDimens = ChartDimensDefaults.chartDimesDefaults(),
    axisConfig: AxisConfig = AxisConfigDefaults.axisConfigDefaults(isSystemInDarkTheme()),
    barConfig: BarConfig = BarConfigDefaults.barConfigDimesDefaults(),
) {
    val barWidth = remember { mutableFloatStateOf(0F) }
    val maxYValueState = rememberSaveable { mutableFloatStateOf(groupedBarData.maxYValue()) }
    val clickedBar = remember { mutableStateOf(Offset(-10F, -10F)) }
    val maxYValue = maxYValueState.floatValue

    val totalItems: Int = groupedBarData.totalItems()
    Canvas(
        modifier = modifier
            .drawBehind {
                if (axisConfig.showAxis) {
                    drawYAxisWithLabels(axisConfig, maxYValue, textColor = axisConfig.textColor)
                }
            }
            .padding(horizontal = barDimens.padding)
            .pointerInput(Unit) {
                detectTapGestures(onPress = { offset ->
                    clickedBar.value = offset
                })
            },
    ) {
        barWidth.floatValue = size.width.div(totalItems.times(1.2F))
        val yScalableFactor = size.height.div(maxYValue)
        val groupedBarDataColor: List<Color> = groupedBarData.flatMap { it.colors }
        val groupedBarDataCount = groupedBarData.flatMap { it.barData }.count()

        if (groupedBarDataColor.count() != groupedBarDataCount) throw Exception("Total colors cannot be more then $groupedBarDataCount")

        groupedBarData.flatMap { it.barData }
            .forEachIndexed { index, data ->
                val topLeft = getTopLeft(index, barWidth.floatValue, size, data.yValue, yScalableFactor)
                val topRight =
                    getTopRight(index, barWidth.floatValue, size, data.yValue, yScalableFactor)
                val barHeight = data.yValue.times(yScalableFactor)

                if (clickedBar.value.x in (topLeft.x..topRight.x)) {
                    onBarClick(data)
                }
                drawRoundRect(
                    cornerRadius = CornerRadius(if (barConfig.hasRoundedCorner) barHeight else 0F),
                    topLeft = topLeft,
                    color = groupedBarDataColor[index],
                    size = Size(barWidth.floatValue, barHeight),
                )

                if (axisConfig.showXLabels) {
                    drawBarLabel(
                        data.xValue,
                        barWidth.floatValue,
                        barHeight,
                        topLeft,
                        groupedBarData.count(),
                        axisConfig.textColor,
                    )
                }
            }
    }
}
