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

package com.niyaj.feature.reports

sealed class ReportsEvent {

    data class SelectDate(val date: String) : ReportsEvent()

    data class OnChangeProductOrderType(val orderType: String = "") : ReportsEvent()

    data class OnChangeCategoryOrderType(val orderType: String = "") : ReportsEvent()

    data class OnSelectCategory(val categoryName: String) : ReportsEvent()

    data object PrintReport : ReportsEvent()

    data object GenerateReport : ReportsEvent()

    data object PrintBarReport : ReportsEvent()

    data object PrintCategoryWiseReport : ReportsEvent()

    data object PrintProductWiseReport : ReportsEvent()

    data object PrintAddressWiseReport : ReportsEvent()

    data object PrintCustomerWiseReport : ReportsEvent()

    data object PrintExpenseWiseReport : ReportsEvent()
}
