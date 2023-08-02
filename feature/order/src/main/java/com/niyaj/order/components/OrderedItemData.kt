package com.niyaj.order.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CurrencyRupee
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.toTime
import com.niyaj.core.ui.R
import com.niyaj.designsystem.theme.LightColor8
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Order
import com.niyaj.ui.components.TextWithIcon

@Composable
fun OrderedItemData(
    shape : Shape,
    order: Order,
    onClickViewDetails: (Int) -> Unit,
    onClickPrintOrder: (Int) -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        shape = shape,
        colors = CardDefaults.elevatedCardColors(
            containerColor = LightColor8
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    TextWithIcon(
                        text = order.orderId.toString(),
                        icon = Icons.Outlined.Tag
                    )

                    order.customerPhone?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        TextWithIcon(
                            text = it,
                            icon = Icons.Outlined.PhoneAndroid
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    TextWithIcon(
                        text = order.orderDate.toTime,
                        icon = Icons.Outlined.AccessTime
                    )
                }

                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    order.customerAddress?.let {
                        TextWithIcon(
                            text = it,
                            icon = Icons.Outlined.Place
                        )
                        Spacer(modifier = Modifier.height(SpaceSmall))
                    }

                    TextWithIcon(
                        text = order.orderPrice.totalPrice.minus(order.orderPrice.discountPrice).toString(),
                        icon = Icons.Outlined.CurrencyRupee
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    FilledTonalIconButton(
                        onClick = {
                            onClickViewDetails(order.orderId)
                        },
                        shape = RoundedCornerShape(SpaceMini),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Visibility,
                            contentDescription = stringResource(id = R.string.order_details),
                        )
                    }

                    Spacer(modifier = Modifier.width(SpaceMini))

                    FilledTonalIconButton(
                        onClick = {
                            onClickPrintOrder(order.orderId)
                        },
                        shape = RoundedCornerShape(SpaceMini),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Print,
                            contentDescription = stringResource(id = R.string.print_order)
                        )
                    }
                }
            }
        }
    }
}