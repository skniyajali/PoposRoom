/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.cart.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.designsystem.components.PoposIconButton
import com.niyaj.designsystem.components.PoposOutlinedButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.LightColor8
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.OrderType

@Composable
fun CartItemTotalPriceSection(
    modifier: Modifier = Modifier,
    itemCount: Int,
    totalPrice: Long,
    orderType: OrderType,
    showPrintBtn: Boolean = true,
    onClickPlaceOrder: () -> Unit = {},
    onClickPrintOrder: () -> Unit = {},
) = trace("CartItemTotalPriceSection") {
    val shape = RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp)
    val containerColor = LightColor8
    val color = if (orderType == OrderType.DineOut) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.secondary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(containerColor, shape)
            .padding(SpaceSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Total",
                style = MaterialTheme.typography.labelMedium,
                color = color
            )

            Text(
                modifier = Modifier.weight(0.8f),
                text = "Rs. $totalPrice",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }


        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            PoposOutlinedButton(
                text = "Place Order",
                onClick = onClickPlaceOrder,
                enabled = itemCount > 0,
                shape = CutCornerShape(4.dp),
                border = BorderStroke(1.dp, color),
                textColor = color
            )

            if (showPrintBtn) {
                Spacer(modifier = Modifier.width(SpaceSmall))

                PoposIconButton(
                    icon = PoposIcons.Print,
                    onClick = onClickPrintOrder,
                    enabled = itemCount > 0,
                    shape = CutCornerShape(4.dp),
                    btnHeight = 30.dp,
                    containerColor = color,
                    contentColor = MaterialTheme.colorScheme.primaryContainer,
                )
            }
        }
    }
}