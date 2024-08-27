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

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.LightColor8
import com.niyaj.designsystem.theme.LightColor9
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.model.CartProductItem
import com.niyaj.ui.parameterProvider.CartPreviewParameterData
import com.niyaj.ui.utils.DevicePreviews

@Composable
fun CartItemProductDetailsSection(
    cartProducts: List<CartProductItem>,
    modifier: Modifier = Modifier,
    decreaseQuantity: (Int) -> Unit = {},
    increaseQuantity: (Int) -> Unit = {},
) = trace("CartItemProductDetailsSection") {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent,
        shape = RoundedCornerShape(4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            cartProducts.forEach { cartProduct ->
                key(cartProduct.productId) {
                    CartProduct(
                        cartProduct = cartProduct,
                        decreaseQuantity = decreaseQuantity,
                        increaseQuantity = increaseQuantity,
                    )
                }
            }
        }
    }
}

@Composable
private fun CartProduct(
    cartProduct: CartProductItem,
    decreaseQuantity: (Int) -> Unit,
    increaseQuantity: (Int) -> Unit,
    modifier: Modifier = Modifier,
    productBoxColor: Color = LightColor9,
    qtyBoxColor: Color = LightColor8,
) = trace("CartProduct") {
    val quantity = animateIntAsState(cartProduct.productQuantity, label = "quantity")
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(SpaceMini),
        tonalElevation = 1.dp,
        shape = RoundedCornerShape(SpaceMini),
        color = Color.Transparent,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(productBoxColor, RoundedCornerShape(4.dp))
                    .weight(2f, true),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(SpaceMini),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.weight(2.2f, true),
                        text = cartProduct.productName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.width(SpaceMini))
                    Text(
                        text = cartProduct.productPrice.toString().toRupee,
                        modifier = Modifier.weight(0.8f, true),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Spacer(modifier = Modifier.width(SpaceMini))

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(qtyBoxColor, RoundedCornerShape(4.dp))
                    .weight(1f, true),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    Arrangement.SpaceBetween,
                    Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = { decreaseQuantity(cartProduct.productId) },
                    ) {
                        Icon(
                            imageVector = if (cartProduct.productQuantity > 1) {
                                PoposIcons.Remove
                            } else {
                                PoposIcons.Delete
                            },
                            contentDescription = "Decrease quantity",
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }

                    Text(
                        text = quantity.value.toString(),
                        softWrap = false,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )

                    IconButton(
                        onClick = { increaseQuantity(cartProduct.productId) },
                    ) {
                        Icon(
                            imageVector = PoposIcons.Add,
                            contentDescription = "Increase quantity",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun CartItemProductDetailsSectionPreview(
    modifier: Modifier = Modifier,
    cartProducts: List<CartProductItem> = CartPreviewParameterData.sampleCartProductItems,
) {
    PoposRoomTheme {
        CartItemProductDetailsSection(
            cartProducts = cartProducts,
            modifier = modifier,
            decreaseQuantity = {},
            increaseQuantity = {},
        )
    }
}

@DevicePreviews
@Composable
private fun CartProductPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        CartProduct(
            cartProduct = CartProductItem(
                productId = 1,
                productName = "Pasta",
                productPrice = 120,
                productQuantity = 2,
            ),
            decreaseQuantity = {},
            increaseQuantity = {},
            modifier = modifier,
        )
    }
}
