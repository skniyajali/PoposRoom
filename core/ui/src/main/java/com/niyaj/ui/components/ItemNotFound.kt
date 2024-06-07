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

package com.niyaj.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.niyaj.designsystem.components.PoposTextButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.utils.DevicePreviews

const val ITEM_TITLE = "Could not find what you were looking for?"
const val ITEM_BTN_TEXT = "Create New Item"

@Composable
fun ItemNotFound(
    modifier: Modifier = Modifier,
    title: String = ITEM_TITLE,
    btnText: String = ITEM_BTN_TEXT,
    icon: ImageVector = PoposIcons.Add,
    onBtnClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(SpaceSmall))

        PoposTextButton(
            text = btnText,
            icon = icon,
            onClick = onBtnClick,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@DevicePreviews
@Composable
fun ItemNotFoundPreview(
    modifier: Modifier = Modifier,
) {
    ItemNotFound { }
}
