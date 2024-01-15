package com.niyaj.order.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
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
import com.niyaj.designsystem.theme.Pewter
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.AddOnItem
import com.niyaj.model.CartProductItem
import com.niyaj.model.Charges
import com.niyaj.model.OrderPrice
import com.niyaj.model.OrderType
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.StandardChip
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.TextDivider
import com.niyaj.ui.components.ThreeGridTexts
import com.niyaj.ui.components.TwoGridTexts

/**
 * This composable displays the cart items
 */
@Composable
fun CartItemDetails(
    modifier: Modifier = Modifier,
    orderType: OrderType,
    doesChargesIncluded: Boolean,
    cartProducts: List<CartProductItem>,
    addOnItems: List<AddOnItem>,
    charges: List<Charges>,
    additionalCharges: List<Charges> = emptyList(),
    orderPrice: OrderPrice,
    doesExpanded : Boolean,
    onExpandChanged : () -> Unit,
) = trace("CartItemDetails") {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
            .clickable {
                onExpandChanged()
            },
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Pewter,
        )
    ){
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = doesExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                IconWithText(
                    text = "Cart Items",
                    icon = Icons.Default.ShoppingBag,
                    isTitle = true
                )
            },
            trailing = {
                StandardChip(text = "${cartProducts.size} Items")
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = {
                        onExpandChanged()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            },
            content = {
                CartItemOrderProductDetails(
                    orderType = orderType,
                    doesChargesIncluded = doesChargesIncluded,
                    cartProducts = cartProducts,
                    addOnItems = addOnItems,
                    charges = charges,
                    additionalCharges = additionalCharges,
                    orderPrice = orderPrice
                )
            },
        )
    }
}

@Composable
fun CartItemOrderProductDetails(
    modifier: Modifier = Modifier,
    orderType: OrderType,
    doesChargesIncluded: Boolean,
    cartProducts: List<CartProductItem>,
    addOnItems: List<AddOnItem>,
    charges: List<Charges>,
    additionalCharges: List<Charges> = emptyList(),
    orderPrice: OrderPrice,
) = trace("CartItemOrderProductDetails") {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
    ) {
        ThreeGridTexts(
            textOne = "Name",
            textTwo = "Qty",
            textThree = "Price",
            isTitle = true
        )
        Spacer(modifier = Modifier.height(SpaceSmall))

        HorizontalDivider(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(SpaceSmall))

        cartProducts.forEach { product ->
            ThreeGridTexts(
                textOne = product.productName,
                textTwo = product.productQuantity.toString(),
                textThree = product.productPrice.toString().toRupee,
            )
            Spacer(modifier = Modifier.height(SpaceSmall))
        }

        if(addOnItems.isNotEmpty()){
            Spacer(modifier = Modifier.height(SpaceSmall))

            TextDivider(
                text = "Add On Items"
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            for(addOnItem in addOnItems){
                TwoGridTexts(
                    textOne = addOnItem.itemName,
                    textTwo = addOnItem.itemPrice.toString().toRupee,
                )
                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }

        if (charges.isNotEmpty()) {
            if (doesChargesIncluded && orderType != OrderType.DineIn) {

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

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(SpaceSmall))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Sub Total",
                style = MaterialTheme.typography.bodySmall,
            )

            Text(
                text = orderPrice.basePrice.toRupee,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
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
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = orderPrice.discountPrice.toRupee,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(SpaceSmall))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 2.dp,
            color = Color.Gray
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
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(SpaceSmall))
    }
}