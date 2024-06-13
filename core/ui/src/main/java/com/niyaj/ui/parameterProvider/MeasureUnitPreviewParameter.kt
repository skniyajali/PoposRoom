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

package com.niyaj.ui.parameterProvider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.niyaj.model.MeasureUnit
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.MeasureUnitPreviewData.measureUnits

class MeasureUnitPreviewParameter : PreviewParameterProvider<UiState<List<MeasureUnit>>> {
    override val values: Sequence<UiState<List<MeasureUnit>>>
        get() = sequenceOf(
            UiState.Loading,
            UiState.Empty,
            UiState.Success(measureUnits),
        )
}

object MeasureUnitPreviewData {
    val measureUnits = listOf(
        MeasureUnit(
            unitId = 1,
            unitName = "Meter",
            unitValue = 1.0,
        ),
        MeasureUnit(
            unitId = 2,
            unitName = "Inch",
            unitValue = 0.0254,
        ),
        MeasureUnit(
            unitId = 3,
            unitName = "Kilogram",
            unitValue = 1.0,
        ),
        MeasureUnit(
            unitId = 4,
            unitName = "Pound",
            unitValue = 0.4,
        ),
        MeasureUnit(
            unitId = 5,
            unitName = "Liter",
            unitValue = 1.0,
        ),
        MeasureUnit(
            unitId = 6,
            unitName = "Gallon",
            unitValue = 3.0,
        ),
        MeasureUnit(
            unitId = 7,
            unitName = "Celsius",
            unitValue = 1.0,
        ),
        MeasureUnit(
            unitId = 8,
            unitName = "Fahrenheit",
            unitValue = 0.5,
        ),
        MeasureUnit(
            unitId = 9,
            unitName = "Kilometer",
            unitValue = 1.0,
        ),
        MeasureUnit(
            unitId = 10,
            unitName = "Mile",
            unitValue = 1.0,
        ),
    )
}
