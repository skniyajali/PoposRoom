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

package com.niyaj.feature.chart.common.axis

import androidx.compose.ui.graphics.Color

data class AxisConfig(
    val showAxis: Boolean,
    val isAxisDashed: Boolean,
    val showUnitLabels: Boolean,
    val showXLabels: Boolean,
    val xAxisColor: Color = Color.LightGray,
    val yAxisColor: Color = Color.LightGray,
    val textColor: Color,
)

internal object AxisConfigDefaults {

    fun axisConfigDefaults(isDarkMode: Boolean) = AxisConfig(
        xAxisColor = Color.LightGray,
        showAxis = true,
        isAxisDashed = false,
        showUnitLabels = true,
        showXLabels = true,
        yAxisColor = Color.LightGray,
        textColor = if (isDarkMode) Color.White else Color.Black,
    )
}
