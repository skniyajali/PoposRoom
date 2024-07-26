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

package com.niyaj.domain.employee

import com.niyaj.common.result.ValidationResult
import com.niyaj.common.tags.EmployeeTestTags
import javax.inject.Inject

class ValidateEmployeeSalaryUseCase @Inject constructor() {
    operator fun invoke(salary: String): ValidationResult {
        if (salary.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_SALARY_EMPTY_ERROR,
            )
        }

        if (salary.length != 5) {
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_SALARY_LENGTH_ERROR,
            )
        }

        if (salary.any { it.isLetter() }) {
            return ValidationResult(
                successful = false,
                errorMessage = EmployeeTestTags.EMPLOYEE_SALARY_LETTER_ERROR,
            )
        }

        return ValidationResult(successful = true)
    }
}
