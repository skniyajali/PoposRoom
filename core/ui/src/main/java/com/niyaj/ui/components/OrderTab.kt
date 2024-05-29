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

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.niyaj.designsystem.icon.PoposIcons

sealed class OrderTab(
    val icon: ImageVector,
    val title: String,
    val showBadge: Boolean = false,
    val screen: @Composable () -> Unit,
) {

    data class DineInOrder(
        val shouldShowBadge: Boolean = false,
        val content: @Composable () -> Unit = {},
    ) : OrderTab(
        icon = PoposIcons.DinnerDining,
        title = "DineIn",
        showBadge = shouldShowBadge,
        screen = {
            content()
        },
    )

    data class DineOutOrder(
        val shouldShowBadge: Boolean = false,
        val content: @Composable () -> Unit = {},
    ) : OrderTab(
        icon = PoposIcons.DeliveryDining,
        title = "DineOut",
        showBadge = shouldShowBadge,
        screen = {
            content()
        },
    )
}
