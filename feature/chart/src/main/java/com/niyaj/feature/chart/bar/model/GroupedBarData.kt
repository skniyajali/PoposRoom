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

package com.niyaj.feature.chart.bar.model

import androidx.compose.ui.graphics.Color

data class GroupedBarData(val barData: List<BarData>, val colors: List<Color> = List(barData.count()) { Color.Transparent })

internal fun List<GroupedBarData>.totalItems(): Int = this.sumOf {
    it.barData.count()
}

internal fun List<GroupedBarData>.maxYValue() = maxOf {
    it.barData.maxYValue()
}
