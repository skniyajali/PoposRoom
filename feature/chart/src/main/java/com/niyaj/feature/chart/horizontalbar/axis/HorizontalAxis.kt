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

package com.niyaj.feature.chart.horizontalbar.axis

import android.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import java.text.DecimalFormat

internal fun DrawScope.horizontalYAxis(
    axisConfig: HorizontalAxisConfig,
    maxValue: Float,
    startAngle: Float,
) {
    val xScaleFactor = size.width.div(4)
//    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(40f, 20f), 0f)
    val axisScaleFactor = maxValue.div(4)
    val graphHeight = size.height

    val list: List<String> = (0..5).toList().map {
        getLabelText(axisScaleFactor.times(it))
    }
    list.forEachIndexed { index, text ->
        if (axisConfig.showUnitLabels) {
            drawIntoCanvas {
                it.nativeCanvas.apply {
                    drawText(
                        text,
                        getXValue(startAngle, xScaleFactor, index),
                        graphHeight.plus(50F),
                        Paint().apply {
                            textSize = size.width.div(30)
                            textAlign = Paint.Align.CENTER
                        },
                    )
                }
            }
        }
    }
}

fun getXValue(startAngle: Float, xScaleFactor: Float, index: Int): Float {
    return if (startAngle == 180F) {
        xScaleFactor.times(index)
    } else {
        xScaleFactor.times(4.minus(index))
    }
}

private fun getLabelText(value: Float) = DecimalFormat("#.##").format(value).toString()
