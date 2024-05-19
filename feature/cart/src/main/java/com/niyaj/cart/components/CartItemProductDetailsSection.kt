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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.LightColor8
import com.niyaj.designsystem.theme.LightColor9
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.model.CartProductItem

@Composable
fun CartItemProductDetailsSection(
    cartProducts: List<CartProductItem>,
    decreaseQuantity: (Int) -> Unit = {},
    increaseQuantity: (Int) -> Unit = {},
) = trace("CartItemProductDetailsSection") {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
    ) {
        cartProducts.forEach { cartProduct ->
            CartProduct(
                cartProduct = cartProduct,
                decreaseQuantity = decreaseQuantity,
                increaseQuantity = increaseQuantity
            )
        }
    }
}

@Composable
fun CartProduct(
    cartProduct: CartProductItem,
    decreaseQuantity: (Int) -> Unit,
    increaseQuantity: (Int) -> Unit,
) = trace("CartProduct") {
    key(cartProduct.productId) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(SpaceMini),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(LightColor9, RoundedCornerShape(4.dp))
                    .weight(2f, true)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(SpaceMini),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
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
                        textAlign = TextAlign.End
                    )
                }
            }


            key(cartProduct.productQuantity) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(LightColor8)
                        .weight(1f, true),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize(),
                        Arrangement.SpaceBetween,
                        Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { decreaseQuantity(cartProduct.productId) },
                        ) {
                            Icon(
                                imageVector = if (cartProduct.productQuantity > 1)
                                    PoposIcons.Remove else PoposIcons.Delete,
                                contentDescription = "Decrease quantity",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }

                        AnimatedCounter(count = cartProduct.productQuantity)

                        IconButton(
                            onClick = { increaseQuantity(cartProduct.productId) },
                        ) {
                            Icon(
                                imageVector = PoposIcons.Add,
                                contentDescription = "Increase quantity",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedCounter(
    count: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.labelLarge
) = trace("AnimatedCounter") {
    var oldCount by remember {
        mutableIntStateOf(count)
    }
    SideEffect {
        oldCount = count
    }
    Row(modifier = modifier) {
        val countString = count.toString()
        val oldCountString = oldCount.toString()

        for (i in countString.indices) {
            val oldChar = oldCountString.getOrNull(i)
            val newChar = countString[i]
            val char = if (oldChar == newChar) {
                oldCountString[i]
            } else {
                countString[i]
            }
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    slideInVertically { it } togetherWith slideOutVertically { -it }
                }, label = "Animated Counter::State"
            ) { newCount ->
                Text(
                    text = newCount.toString(),
                    style = style,
                    softWrap = false,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}