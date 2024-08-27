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

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.AddOnItem
import com.niyaj.model.CartProductItem
import com.niyaj.model.Charges
import com.niyaj.model.OrderPrice
import com.niyaj.model.OrderType

/**
 * This composable displays the cart items
 */
@Composable
fun CartItemDetails(
    orderType: OrderType,
    orderPrice: OrderPrice,
    expanded: Boolean,
    chargesIncluded: Boolean,
    cartProducts: List<CartProductItem>,
    addOnItems: List<AddOnItem>,
    charges: List<Charges>,
    onExpandChanged: () -> Unit,
    modifier: Modifier = Modifier,
    additionalCharges: List<Charges> = emptyList(),
    containerColor: Color = MaterialTheme.colorScheme.background,
) = trace("CartItemDetails") {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onExpandChanged()
            },
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
        ),
    ) {
        StandardExpandable(
            expanded = expanded,
            onExpandChanged = {
                onExpandChanged()
            },
            content = {
                CartItemOrderProductDetails(
                    orderType = orderType,
                    orderPrice = orderPrice,
                    chargesIncluded = chargesIncluded,
                    cartProducts = cartProducts,
                    addOnItems = addOnItems,
                    charges = charges,
                    additionalCharges = additionalCharges,
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            rowClickable = true,
            title = {
                IconWithText(
                    text = "Cart Items",
                    icon = PoposIcons.ShoppingBag,
                    isTitle = true,
                )
            },
            trailing = {
                StandardChip(text = "${cartProducts.size} Items")
            },
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = {
                        onExpandChanged()
                    },
                ) {
                    Icon(
                        imageVector = PoposIcons.ArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                }
            },
        )
    }
}

@Composable
internal fun CartItemOrderProductDetails(
    orderType: OrderType,
    orderPrice: OrderPrice,
    chargesIncluded: Boolean,
    cartProducts: List<CartProductItem>,
    addOnItems: List<AddOnItem>,
    charges: List<Charges>,
    modifier: Modifier = Modifier,
    additionalCharges: List<Charges> = emptyList(),
) = trace("CartItemOrderProductDetails") {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
    ) {
        Spacer(modifier = Modifier.height(SpaceSmall))
        DashedDivider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceSmall))

        ThreeGridTexts(
            textOne = "Name",
            textTwo = "Qty",
            textThree = "Price",
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(SpaceSmall))
        DashedDivider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceSmall))

        cartProducts.forEach { product ->
            val productPrice = (product.productPrice * product.productQuantity)
            ThreeGridTexts(
                textOne = product.productName,
                textTwo = product.productQuantity.toString(),
                textThree = productPrice.toRupee,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(SpaceSmall))
        }

        if (addOnItems.isNotEmpty()) {
            Spacer(modifier = Modifier.height(SpaceSmall))

            TextDivider(text = "Add On Items")

            Spacer(modifier = Modifier.height(SpaceSmall))

            for (addOnItem in addOnItems) {
                TwoGridTexts(
                    textOne = addOnItem.itemName,
                    textTwo = addOnItem.itemPrice.toString().toRupee,
                )
                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }

        if (charges.isNotEmpty()) {
            if (chargesIncluded && orderType == OrderType.DineOut) {
                val showText = charges.any { it.isApplicable }

                if (showText) {
                    Spacer(modifier = Modifier.height(SpaceSmall))

                    TextDivider(text = "Charges")

                    Spacer(modifier = Modifier.height(SpaceSmall))
                }

                for (charge in charges) {
                    if (charge.isApplicable) {
                        TwoGridTexts(
                            textOne = charge.chargesName,
                            textTwo = charge.chargesPrice.toString().toRupee,
                        )
                        Spacer(modifier = Modifier.height(SpaceSmall))
                    }
                }
            }
        }

        if (additionalCharges.isNotEmpty()) {
            Spacer(modifier = Modifier.height(SpaceSmall))

            TextDivider(text = "Additional Charges")

            Spacer(modifier = Modifier.height(SpaceSmall))

            for (charge in additionalCharges) {
                TwoGridTexts(
                    textOne = charge.chargesName,
                    textTwo = charge.chargesPrice.toString().toRupee,
                )
                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }

        Spacer(modifier = Modifier.height(SpaceSmall))
        DashedDivider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceSmall))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Sub Total",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                text = orderPrice.basePrice.toRupee,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(modifier = Modifier.height(SpaceSmall))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Discount",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                text = orderPrice.discountPrice.toRupee,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(modifier = Modifier.height(SpaceSmall))

        DashedDivider(
            modifier = Modifier.fillMaxWidth(),
            dashHeight = 2.dp,
        )

        Spacer(modifier = Modifier.height(SpaceSmall))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Total",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = (orderPrice.basePrice.minus(orderPrice.discountPrice)).toRupee,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }

        Spacer(modifier = Modifier.height(SpaceSmall))

        DashedDivider(
            modifier = Modifier.fillMaxWidth(),
            dashHeight = 2.dp,
        )

        Spacer(modifier = Modifier.height(SpaceSmall))
    }
}
