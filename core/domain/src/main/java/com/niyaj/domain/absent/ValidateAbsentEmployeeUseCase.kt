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

package com.niyaj.domain.absent

import com.niyaj.common.result.ValidationResult
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_EMPLOYEE_NAME_EMPTY
import javax.inject.Inject

class ValidateAbsentEmployeeUseCase @Inject constructor() {
    operator fun invoke(employeeId: Int): ValidationResult {
        if (employeeId == 0) {
            return ValidationResult(
                successful = false,
                errorMessage = ABSENT_EMPLOYEE_NAME_EMPTY,
            )
        }

        return ValidationResult(
            successful = true,
        )
    }
}
