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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.components.PoposIconTextButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.LightColor10
import com.niyaj.designsystem.theme.SpaceSmall

@Composable
internal fun CartFooterPlaceOrder(
    modifier: Modifier = Modifier,
    countTotalItems: Int,
    countSelectedItem: Int,
    showPrintBtn: Boolean,
    onClickSelectAll: () -> Unit,
    onClickPlaceAllOrder: () -> Unit,
    onClickPrintAllOrder: () -> Unit,
) = trace("CartFooterPlaceOrder") {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = LightColor10,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = onClickSelectAll,
                ) {
                    Icon(
                        imageVector = if (countTotalItems == countSelectedItem) {
                            PoposIcons.CheckCircle
                        } else {
                            PoposIcons.CheckCircleOutline
                        },
                        contentDescription = "Select All Order",
                        tint = if (countTotalItems == countSelectedItem) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        },
                    )
                }
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                            ),
                        ) {
                            append("$countTotalItems")
                            append(" - ")
                        }

                        append("$countSelectedItem")

                        append(" Selected")
                    },
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClickSelectAll,
                    ),
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val text =
                    if (countSelectedItem == 0) " " else if
                            (countSelectedItem < countTotalItems) " $countSelectedItem " else " All "

                PoposButton(
                    text = "Place${text}Order",
                    onClick = onClickPlaceAllOrder,
                    enabled = countSelectedItem > 0,
                    shape = CutCornerShape(4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    btnHeight = ButtonDefaults.MinHeight,
                )

                if (showPrintBtn) {
                    Spacer(modifier = Modifier.width(SpaceSmall))

                    PoposIconTextButton(
                        icon = PoposIcons.Print,
                        enabled = countSelectedItem > 0,
                        onClick = onClickPrintAllOrder,
                        contentDescription = "Print Order",
                        btnHeight = 30.dp,
                        shape = CutCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                        ),
                    )
                }
            }
        }
    }
}
