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

package com.niyaj.cart.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.niyaj.designsystem.icon.PoposIcons

typealias ComposableFunction = @Composable () -> Unit

sealed class CartTabItem(
    val icon: ImageVector,
    val title: String,
    val screen: ComposableFunction,
) {
    data class DineInItem(
        val content: @Composable () -> Unit = {},
    ) : CartTabItem(
        icon = PoposIcons.DinnerDining,
        title = "DineIn",
        screen = {
            content()
        },
    )

    data class DineOutItem(
        val content: @Composable () -> Unit = {},
    ) : CartTabItem(
        icon = PoposIcons.DeliveryDining,
        title = "DineOut",
        screen = {
            content()
        },
    )
}
