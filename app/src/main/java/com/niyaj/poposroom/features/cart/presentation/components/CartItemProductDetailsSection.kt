package com.niyaj.poposroom.features.cart.presentation.components

import androidx.compose.animation.Crossfade
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.niyaj.poposroom.features.cart.domain.model.CartProductItem
import com.niyaj.poposroom.features.common.ui.theme.LightColor10
import com.niyaj.poposroom.features.common.ui.theme.SpaceMini
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall
import com.niyaj.poposroom.features.common.utils.toRupee

@Composable
fun CartItemProductDetailsSection(
    cartProducts: List<CartProductItem>,
    decreaseQuantity: (Int) -> Unit = {},
    increaseQuantity: (Int) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpaceSmall)
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
) {
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
                    .background(LightColor10, RoundedCornerShape(4.dp))
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
                                    Icons.Default.Remove else Icons.Default.Delete,
                                contentDescription = "Decrease quantity",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }

                        Crossfade(targetState = cartProduct.productQuantity, label = "Product quantity") {
                            Text(
                                text = it.toString(),
                                fontWeight = FontWeight.SemiBold,
                            )
                        }

                        IconButton(
                            onClick = { increaseQuantity(cartProduct.productId) },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
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