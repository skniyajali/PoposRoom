package com.niyaj.feature.chart.horizontalbar.common

import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.niyaj.common.utils.getAllCapitalizedLetters
import com.niyaj.feature.chart.horizontalbar.model.HorizontalBarData

internal fun DrawScope.drawHorizontalBarLabel(
    horizontalBarData: HorizontalBarData,
    barHeight: Float,
    topLeft: Offset,
    labelTextColor: Color,
) {
    drawIntoCanvas {
        it.nativeCanvas.apply {
            drawText(
                getAllCapitalizedLetters(horizontalBarData.yValue.toString()).take(3),
                0F.minus(barHeight.div(1.8F)),
                topLeft.y.plus(barHeight.div(2)),
                Paint().apply {
                    color = labelTextColor.toArgb()
                    textSize = size.width.div(30)
                    textAlign = Paint.Align.CENTER
                }
            )
        }
    }
}

internal fun DrawScope.drawLineLabels(
    offset: Offset,
    horizontalBarData: HorizontalBarData,
    lineLabelColor: Pair<Color, Color>,
) {
    val textSp = size.width.div(25)
    drawIntoCanvas {
        it.nativeCanvas.apply {
            drawText(
                "${horizontalBarData.yValue} - ${horizontalBarData.xValue.toString().substringBefore(".")} Qty",
                offset.x,
                offset.y,
                Paint().apply {
                    color = lineLabelColor.first.toArgb()
                    textSize = textSp
                    textAlign = Paint.Align.CENTER
                }
            )
        }
    }
}