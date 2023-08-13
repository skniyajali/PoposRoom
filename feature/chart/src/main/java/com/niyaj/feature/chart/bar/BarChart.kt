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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import com.niyaj.feature.chart.bar.common.calculations.getTopLeft
import com.niyaj.feature.chart.bar.common.calculations.getTopRight
import com.niyaj.feature.chart.bar.common.component.drawBarLabel
import com.niyaj.feature.chart.bar.config.BarConfig
import com.niyaj.feature.chart.bar.config.BarConfigDefaults
import com.niyaj.feature.chart.bar.model.BarData
import com.niyaj.feature.chart.bar.model.maxYValue
import com.niyaj.feature.chart.common.axis.AxisConfig
import com.niyaj.feature.chart.common.axis.AxisConfigDefaults
import com.niyaj.feature.chart.common.axis.drawYAxisWithLabels
import com.niyaj.feature.chart.common.dimens.ChartDimens
import com.niyaj.feature.chart.common.dimens.ChartDimensDefaults

@Composable
fun BarChart(
    barData: List<BarData>,
    color: Color,
    onBarClick: (BarData) -> Unit,
    modifier: Modifier = Modifier,
    chartDimens: ChartDimens = ChartDimensDefaults.chartDimesDefaults(),
    axisConfig: AxisConfig = AxisConfigDefaults.axisConfigDefaults(isSystemInDarkTheme()),
    barConfig: BarConfig = BarConfigDefaults.barConfigDimesDefaults()
) {
    BarChart(
        barData = barData,
        colors = listOf(color, color),
        onBarClick = onBarClick,
        modifier = modifier,
        chartDimens = chartDimens,
        axisConfig = axisConfig,
        barConfig = barConfig
    )
}

@Composable
fun BarChart(
    barData: List<BarData>,
    colors: List<Color>,
    onBarClick: (BarData) -> Unit,
    modifier: Modifier = Modifier,
    chartDimens: ChartDimens = ChartDimensDefaults.chartDimesDefaults(),
    axisConfig: AxisConfig = AxisConfigDefaults.axisConfigDefaults(isSystemInDarkTheme()),
    barConfig: BarConfig = BarConfigDefaults.barConfigDimesDefaults()
) {
    val maxYValueState = rememberSaveable { mutableFloatStateOf(barData.maxYValue()) }
    val clickedBar = remember { mutableStateOf(Offset(-10F, -10F)) }

    val maxYValue = maxYValueState.floatValue
    val barWidth = remember { mutableFloatStateOf(0F) }

//    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .drawBehind {
                if (axisConfig.showAxis) {
                    drawYAxisWithLabels(
                        axisConfig = axisConfig,
                        maxValue = maxYValue,
                        textColor = axisConfig.textColor
                    )
                }
            }
            .padding(horizontal = chartDimens.padding)
            .pointerInput(Unit) {
                detectTapGestures(onPress = { offset ->
                    clickedBar.value = offset
                })
            }
    ) {
        barWidth.floatValue = size.width.div(barData.count().times(1.2F))
        val yScalableFactor = size.height.div(maxYValue)

        barData.forEachIndexed { index, data ->
            val topLeft = getTopLeft(index, barWidth.floatValue, size, data.yValue, yScalableFactor)
            val topRight = getTopRight(index, barWidth.floatValue, size, data.yValue, yScalableFactor)
            val barHeight = data.yValue.times(yScalableFactor)

            if (clickedBar.value.x in (topLeft.x..topRight.x)) {
                onBarClick(data)
            }

            drawRoundRect(
//                cornerRadius = CornerRadius(if (barConfig.hasRoundedCorner) barHeight else 0F),
                cornerRadius = CornerRadius(x = 4F, y =  4F),
                topLeft = topLeft,
                brush = Brush.linearGradient(colors),
                size = Size(barWidth.floatValue, barHeight)
            )

//            drawText(
//                textMeasurer = textMeasurer,
//                text = data.yValue.toString().substringBefore(".").toRupee,
//                topLeft = Offset(topLeft.x, topLeft.y),
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//                style = TextStyle(
//                    color = Color.Black,
//                    fontWeight = FontWeight.Normal,
//                ),
//                maxSize = IntSize(barWidth.value.toInt(), barHeight.toInt())
//            )


            if (axisConfig.showXLabels) {
                drawBarLabel(
                    data.xValue,
                    barWidth.floatValue,
                    barHeight,
                    topLeft,
                    barData.count(),
                    axisConfig.textColor
                )
            }
        }
    }
}
