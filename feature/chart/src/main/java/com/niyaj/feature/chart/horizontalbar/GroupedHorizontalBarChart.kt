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

package com.niyaj.feature.chart.horizontalbar

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import com.niyaj.feature.chart.common.dimens.ChartDimens
import com.niyaj.feature.chart.common.dimens.ChartDimensDefaults
import com.niyaj.feature.chart.horizontalbar.axis.HorizontalAxisConfig
import com.niyaj.feature.chart.horizontalbar.axis.HorizontalAxisConfigDefaults
import com.niyaj.feature.chart.horizontalbar.axis.horizontalYAxis
import com.niyaj.feature.chart.horizontalbar.common.drawHorizontalBarLabel
import com.niyaj.feature.chart.horizontalbar.common.getBottomLeft
import com.niyaj.feature.chart.horizontalbar.common.getTopLeft
import com.niyaj.feature.chart.horizontalbar.config.HorizontalBarConfig
import com.niyaj.feature.chart.horizontalbar.config.HorizontalBarConfigDefaults
import com.niyaj.feature.chart.horizontalbar.config.StartDirection
import com.niyaj.feature.chart.horizontalbar.model.GroupedHorizontalBarData
import com.niyaj.feature.chart.horizontalbar.model.HorizontalBarData
import com.niyaj.feature.chart.horizontalbar.model.maxXValue
import com.niyaj.feature.chart.horizontalbar.model.totalItems

@Composable
fun GroupedHorizontalBarChart(
    groupedBarData: List<GroupedHorizontalBarData>,
    modifier: Modifier = Modifier,
    onBarClick: (HorizontalBarData) -> Unit = {},
    barDimens: ChartDimens = ChartDimensDefaults.horizontalChartDimesDefaults(),
    horizontalAxisConfig: HorizontalAxisConfig = HorizontalAxisConfigDefaults.axisConfigDefaults(),
    horizontalBarConfig: HorizontalBarConfig = HorizontalBarConfigDefaults.horizontalBarConfig(),
) {
    val startAngle = if (horizontalBarConfig.startDirection == StartDirection.Left) 180F else 0F
    val maxXValueState = rememberSaveable { mutableFloatStateOf(groupedBarData.maxXValue()) }
    val clickedBar = remember { mutableStateOf(Offset(-10F, -10F)) }
    val maxXValue = maxXValueState.floatValue
    val barHeight = remember { mutableFloatStateOf(0F) }
    val totalItems: Int = groupedBarData.totalItems()
    val labelTextColor = if (isSystemInDarkTheme()) Color.White else Color.Black

    Canvas(
        modifier = modifier
            .drawBehind {
                if (horizontalAxisConfig.showAxes) {
                    horizontalYAxis(horizontalAxisConfig, maxXValue, startAngle)
                }
            }
            .padding(horizontal = barDimens.padding)
            .pointerInput(Unit) {
                detectTapGestures(onPress = { offset ->
                    clickedBar.value = offset
                })
            },
    ) {
        barHeight.floatValue = size.height.div(totalItems.times(1.2F))
        val xScalableFactor = size.width.div(maxXValue)
        val groupedHorizontalBarDataColor: List<Color> = groupedBarData.flatMap { it.colors }
        val groupedBarDataCount = groupedBarData.flatMap { it.horizontalBarData }.count()

        if (groupedHorizontalBarDataColor.count() != groupedBarDataCount) {
            throw Exception("Total colors cannot be more then $groupedBarDataCount")
        }

        groupedBarData.flatMap { it.horizontalBarData }
            .forEachIndexed { index, data ->
                when (horizontalBarConfig.startDirection) {
                    StartDirection.Right -> {
                        val topLeft = getTopLeft(index, barHeight, size, data, xScalableFactor)
                        val bottomLeft =
                            getBottomLeft(index, barHeight, size, data, xScalableFactor)
                        val barWidth = data.xValue.times(xScalableFactor)

                        if (clickedBar.value.y in (topLeft.y..bottomLeft.y)) {
                            onBarClick(data)
                        }
                        drawBars(
                            data,
                            barHeight.floatValue,
                            color = groupedHorizontalBarDataColor[index],
                            horizontalBarConfig.showLabels,
                            topLeft,
                            barWidth,
                            labelTextColor,
                        )
                    }
                    else -> {
                        val barWidth = data.xValue.times(xScalableFactor)
                        val topLeft = Offset(0F, barHeight.floatValue.times(index).times(1.2F))
                        val bottomLeft =
                            getBottomLeft(index, barHeight, size, data, xScalableFactor)

                        if (clickedBar.value.y in (topLeft.y..bottomLeft.y)) {
                            onBarClick(data)
                        }
                        drawBars(
                            data,
                            barHeight.floatValue,
                            color = groupedHorizontalBarDataColor[index],
                            horizontalBarConfig.showLabels,
                            topLeft = topLeft,
                            barWidth = barWidth,
                            labelTextColor = labelTextColor,
                        )
                    }
                }
            }
    }
}

private fun DrawScope.drawBars(
    horizontalBarData: HorizontalBarData,
    barHeight: Float,
    color: Color,
    showLabels: Boolean,
    topLeft: Offset,
    barWidth: Float,
    labelTextColor: Color,
) {
    drawRoundRect(
        topLeft = topLeft,
        color = color,
        size = Size(barWidth, barHeight),
    )
    if (showLabels) {
        drawHorizontalBarLabel(
            horizontalBarData = horizontalBarData,
            barHeight = barHeight,
            topLeft = topLeft,
            labelTextColor = labelTextColor,
        )
    }
}
