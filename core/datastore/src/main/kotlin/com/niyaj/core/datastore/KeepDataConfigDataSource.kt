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

package com.niyaj.core.datastore

import androidx.datastore.core.DataStore
import com.niyaj.common.utils.ifZero
import com.niyaj.model.KeepDataConfig
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class KeepDataConfigDataSource @Inject constructor(
    private val dataConfigPreferences: DataStore<KeepDataConfigPreferences>,
) {

    val keepDataConfig = dataConfigPreferences.data.map {
        KeepDataConfig(
            reportInterval = it.reportInterval.ifZero { 7 },
            orderInterval = it.orderInterval.ifZero { 7 },
            cartInterval = it.cartInterval.ifZero { 7 },
            expenseInterval = it.expenseInterval.ifZero { 7 },
            marketListInterval = it.marketListInterval.ifZero { 7 },
            deleteDataBeforeInterval = it.deleteDataBeforeInterval,
        )
    }

    val deleteDataBeforeInterval = dataConfigPreferences.data.map { it.deleteDataBeforeInterval }

    suspend fun updateKeepDataConfig(keepDataConfig: KeepDataConfig) {
        dataConfigPreferences.updateData {
            it.copy {
                reportInterval = keepDataConfig.reportInterval
                orderInterval = keepDataConfig.orderInterval
                cartInterval = keepDataConfig.cartInterval
                expenseInterval = keepDataConfig.expenseInterval
                marketListInterval = keepDataConfig.marketListInterval
                deleteDataBeforeInterval = keepDataConfig.deleteDataBeforeInterval
            }
        }
    }
}
