package com.niyaj.order.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.LightColor9
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Order
import de.charlex.compose.RevealSwipe

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OrderedItem(
    modifier: Modifier = Modifier,
    order: Order,
    onClickPrintOrder: (Int) -> Unit,
    onMarkedAsProcessing: (Int) -> Unit,
    onClickDelete: (Int) -> Unit,
    onClickViewDetails: (Int) -> Unit,
    onClickEdit: (Int) -> Unit,
    onClickShareOrder: (Int) -> Unit,
) {
    RevealSwipe(
        modifier = modifier
            .fillMaxWidth(),
        onContentClick = {
            onClickViewDetails(order.orderId)
        },
        maxRevealDp = 150.dp,
        hiddenContentStart = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
                horizontalArrangement = Arrangement.spacedBy(SpaceSmall, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = {
                        onMarkedAsProcessing(order.orderId)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Restore,
                        contentDescription = "Mark as processing",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = {
                        onClickEdit(order.orderId)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Order",
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        },
        hiddenContentEnd = {
            IconButton(
                onClick = {
                    onClickDelete(order.orderId)
                },
                modifier = Modifier.padding(horizontal = 25.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete order",
                    tint = MaterialTheme.colorScheme.onSecondary,
                )
            }
        },
        animateBackgroundCardColor = true,
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
            onClickPrintOrder = onClickPrintOrder,
            onClickShareOrder = onClickShareOrder,
        )
    }
}