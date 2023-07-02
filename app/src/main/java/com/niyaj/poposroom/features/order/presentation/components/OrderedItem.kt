package com.niyaj.poposroom.features.order.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.niyaj.poposroom.features.common.ui.theme.LightColor9
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall
import com.niyaj.poposroom.features.order.domain.model.Order
import de.charlex.compose.RevealSwipe

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OrderedItem(
    order: Order,
    onClickPrintOrder: (Int) -> Unit,
    onMarkedAsProcessing: (Int) -> Unit,
    onClickDelete: (Int) -> Unit,
    onClickViewDetails: (Int) -> Unit,
    onClickEdit: (Int) -> Unit,
) {
    RevealSwipe(
        modifier = Modifier
            .fillMaxWidth(),
        onContentClick = {},
        maxRevealDp = 150.dp,
        hiddenContentStart = {
            IconButton(
                onClick = {
                    onMarkedAsProcessing(order.orderId)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 25.dp),
                )
            }

            Spacer(modifier = Modifier.width(SpaceSmall))

            IconButton(
                onClick = {
                    onClickEdit(order.orderId)
                }
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }
        },
        hiddenContentEnd = {
            IconButton(
                onClick = {
                    onClickDelete(order.orderId)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete order",
                    modifier = Modifier.padding(horizontal = 25.dp),
                )
            }
        },
        contentColor = MaterialTheme.colorScheme.onSurface,
        backgroundCardContentColor = LightColor9,
        backgroundCardStartColor = MaterialTheme.colorScheme.primary,
        backgroundCardEndColor = MaterialTheme.colorScheme.secondary,
        shape = RoundedCornerShape(6.dp),
        backgroundStartActionLabel = "Start",
        backgroundEndActionLabel = "End",
    ) {
        OrderedItemData(
            shape = it,
            order = order,
            onClickViewDetails = onClickViewDetails,
            onClickPrintOrder = onClickPrintOrder
        )
    }
}