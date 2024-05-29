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

package com.niyaj.employee.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toBarDate
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Payment
import com.niyaj.model.PaymentMode
import com.niyaj.ui.components.IconBox
import com.niyaj.ui.components.StandardOutlinedChip

@Composable
fun EmployeePayment(
    payment: Payment,
) = trace("EmployeePayment") {
    Row(
        modifier = Modifier
            .testTag("Payment Tag")
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = payment.paymentAmount.toRupee,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.8F),
        )

        Text(
            text = payment.paymentDate.toBarDate,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.weight(0.8F),
        )

        Row(
            modifier = Modifier.weight(1.4F),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ) {
            IconBox(
                text = payment.paymentMode.name,
                icon = when (payment.paymentMode) {
                    PaymentMode.Cash -> PoposIcons.Money
                    PaymentMode.Online -> PoposIcons.AccountBalance
                    else -> PoposIcons.Payments
                },
                selected = false,
            )

            Spacer(modifier = Modifier.width(SpaceSmall))

            StandardOutlinedChip(text = payment.paymentType.name)
        }
    }
}
