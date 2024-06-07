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

package com.niyaj.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Account

@Composable
fun AccountInfo(
    modifier: Modifier = Modifier,
    account: Account,
    containerColor: Color = MaterialTheme.colorScheme.background,
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(SpaceSmall),
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
                horizontalArrangement = Arrangement.spacedBy(SpaceSmall, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = PoposIcons.ManageAccounts,
                    contentDescription = "Account info icon",
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Account Info",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            AccountInfoBox(
                title = "Email",
                icon = PoposIcons.Email,
                value = account.email,
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            AccountInfoBox(
                title = "Phone",
                icon = PoposIcons.PhoneAndroid,
                value = account.phone,
            )

            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            AccountInfoBox(
                title = "Password",
                icon = PoposIcons.Password,
                value = account.password,
            )

            Spacer(modifier = Modifier.height(SpaceMini))
        }
    }
}
