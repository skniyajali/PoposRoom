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

package com.niyaj.employee.settings

import com.niyaj.model.Employee

sealed class EmployeeSettingsEvent {

    data object GetExportedItems : EmployeeSettingsEvent()

    data class OnImportEmployeeItemsFromFile(val data: List<Employee>) : EmployeeSettingsEvent()

    data object ImportEmployeeItemsToDatabase : EmployeeSettingsEvent()
}
