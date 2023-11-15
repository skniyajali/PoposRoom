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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.LightColor8
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.OrderType

@Composable
fun CartItemTotalPriceSection(
    modifier: Modifier = Modifier,
    itemCount: Int = 0,
    basePrice: Long = 0,
    discountPrice: Long = 0,
    orderType: OrderType = OrderType.DineIn,
    showPrintBtn: Boolean = true,
    onClickPlaceOrder: () -> Unit = {},
    onClickPrintOrder: () -> Unit = {},
) {
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
                text = "Rs. ${basePrice.minus(discountPrice)}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }


        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { onClickPlaceOrder() },
                enabled = itemCount > 0,
                shape = CutCornerShape(4.dp),
                border = BorderStroke(1.dp, color)
            ) {
                Text(
                    text = "Place Order".uppercase(),
//                    style = MaterialTheme.typography.labelSmall,
                    color = color
                )
            }

            if (showPrintBtn) {
                Spacer(modifier = Modifier.width(SpaceSmall))

                IconButton(
                    onClick = {
                        onClickPrintOrder()
                    },
                    enabled = itemCount > 0,
                    modifier = Modifier
                        .background(color, CutCornerShape(4.dp))
                        .heightIn(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = "Print Order",
                        tint = MaterialTheme.colorScheme.primaryContainer,
                    )
                }
            }
        }
    }
}