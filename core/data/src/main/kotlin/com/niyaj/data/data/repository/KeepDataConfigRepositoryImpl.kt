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

package com.niyaj.data.data.repository

import com.niyaj.common.result.Resource
import com.niyaj.common.result.ValidationResult
import com.niyaj.core.datastore.KeepDataConfigDataSource
import com.niyaj.data.repository.KeepDataConfigRepository
import com.niyaj.data.repository.validation.KeepDataConfigValidationRepository
import com.niyaj.model.KeepDataConfig
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class KeepDataConfigRepositoryImpl @Inject constructor(
    private val config: KeepDataConfigDataSource,
) : KeepDataConfigRepository, KeepDataConfigValidationRepository {

    override val keepDataConfig: Flow<KeepDataConfig> = config.keepDataConfig

    override val shouldDeleteData: Flow<Boolean> = config.deleteDataBeforeInterval

    override suspend fun updateKeepDataConfig(keepDataConfig: KeepDataConfig): Resource<Boolean> {
        val reportResult = validateReportInterval(keepDataConfig.reportInterval)
        val orderResult = validateOrderInterval(keepDataConfig.orderInterval)
        val cartResult = validateCartInterval(keepDataConfig.cartInterval)
        val expenseResult = validateExpenseInterval(keepDataConfig.expenseInterval)
        val marketListResult = validateMarketListInterval(keepDataConfig.marketListInterval)

        val result = listOf(reportResult, orderResult, cartResult, expenseResult, marketListResult)
            .any { !it.successful }

        return if (result) {
            Resource.Error("Unable to validate data")
        } else {
            config.updateKeepDataConfig(keepDataConfig)
            Resource.Success(true)
        }
    }

    override fun validateReportInterval(interval: Int): ValidationResult {
        if (interval == 0) {
            return ValidationResult(
                successful = false,
                errorMessage = "Report interval should be greater than 0",
            )
        }

        if (interval > 30) {
            return ValidationResult(
                successful = false,
                errorMessage = "Report interval should be less than 30 days",
            )
        }

        return ValidationResult(successful = true)
    }

    override fun validateOrderInterval(interval: Int): ValidationResult {
        if (interval == 0) {
            return ValidationResult(
                successful = false,
                errorMessage = "Order interval should be greater than 0",
            )
        }

        if (interval > 30) {
            return ValidationResult(
                successful = false,
                errorMessage = "Order interval should be less than 30 days",
            )
        }

        return ValidationResult(successful = true)
    }

    override fun validateCartInterval(interval: Int): ValidationResult {
        if (interval == 0) {
            return ValidationResult(
                successful = false,
                errorMessage = "Cart interval should be greater than 0",
            )
        }

        if (interval > 30) {
            return ValidationResult(
                successful = false,
                errorMessage = "Cart interval should be less than 30 days",
            )
        }

        return ValidationResult(successful = true)
    }

    override fun validateExpenseInterval(interval: Int): ValidationResult {
        if (interval == 0) {
            return ValidationResult(
                successful = false,
                errorMessage = "Expense interval should be greater than 0",
            )
        }

        if (interval > 30) {
            return ValidationResult(
                successful = false,
                errorMessage = "Expense interval should be less than 30 days",
            )
        }

        return ValidationResult(successful = true)
    }

    override fun validateMarketListInterval(interval: Int): ValidationResult {
        if (interval == 0) {
            return ValidationResult(
                successful = false,
                errorMessage = "Market list interval should be greater than 0",
            )
        }

        if (interval > 30) {
            return ValidationResult(
                successful = false,
                errorMessage = "Market list interval should be less than 30 days",
            )
        }

        return ValidationResult(successful = true)
    }
}
