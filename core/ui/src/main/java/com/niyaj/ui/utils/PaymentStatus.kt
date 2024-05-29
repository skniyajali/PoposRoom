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

package com.niyaj.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.ui.graphics.vector.ImageVector

sealed class PaymentStatus(val status: String, val icon: ImageVector, val order: Int) {

    data object NotPaid : PaymentStatus(status = "Not Paid", icon = Icons.Default.Close, order = 1)

    data object Absent : PaymentStatus(status = "Absent", icon = Icons.Default.EventBusy, order = 2)

    data object Paid : PaymentStatus(status = "Paid", icon = Icons.Default.HowToReg, order = 3)
}
