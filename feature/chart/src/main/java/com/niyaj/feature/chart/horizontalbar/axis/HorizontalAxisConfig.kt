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

import androidx.compose.ui.graphics.Color

data class HorizontalAxisConfig(
    val showAxes: Boolean,
    val showUnitLabels: Boolean,
    val xAxisColor: Color = Color.LightGray,
    val yAxisColor: Color = Color.LightGray,
)

internal object HorizontalAxisConfigDefaults {

    fun axisConfigDefaults() = HorizontalAxisConfig(
        xAxisColor = Color.LightGray,
        showAxes = true,
        showUnitLabels = true,
        yAxisColor = Color.LightGray,
    )
}
