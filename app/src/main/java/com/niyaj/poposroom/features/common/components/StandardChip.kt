package com.niyaj.poposroom.features.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.niyaj.poposroom.features.common.ui.theme.IconSizeSmall
import com.niyaj.poposroom.features.common.ui.theme.SpaceMini
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall
import com.niyaj.poposroom.features.common.utils.Constants.NOT_PAID
import com.niyaj.poposroom.features.common.utils.Constants.PAID
import com.niyaj.poposroom.features.utils.PaymentStatus

@Composable
fun StandardOutlinedChip(
    modifier: Modifier = Modifier,
    text: String = "",
    secondaryText: String? = null,
    isToggleable: Boolean = true,
    isSelected: Boolean = false,
    selectedColor: Color = MaterialTheme.colorScheme.secondary,
    dissectedColor: Color = MaterialTheme.colorScheme.onSecondary,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(if (isSelected) selectedColor else dissectedColor)
            .border(if (isSelected) 0.dp else 1.dp, selectedColor, RoundedCornerShape(2.dp))
            .toggleable(isToggleable) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(SpaceMini),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if(isSelected){
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "$text added",
                    tint = dissectedColor,
                    modifier = Modifier.size(IconSizeSmall)
                )

                Spacer(modifier = Modifier.width(SpaceSmall))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                color = if (isSelected) dissectedColor else selectedColor,
            )

            if(!secondaryText.isNullOrEmpty() && text.startsWith("Cold")){
                Text(
                    text = " Rs. $secondaryText",
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = if (isSelected) dissectedColor else selectedColor,
                )
            }
        }
    }
}


@Composable
fun PaymentStatusChip(
    modifier: Modifier = Modifier,
    isPaid: Boolean = false,
    text: String = if (isPaid) PAID else NOT_PAID,
    paidColor: Color = MaterialTheme.colorScheme.primary,
    notPaidColor: Color = MaterialTheme.colorScheme.secondary,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(if (isPaid) paidColor else notPaidColor),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(SpaceMini),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if(isPaid){
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = text,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(IconSizeSmall)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = text,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(IconSizeSmall)
                )
            }

            Spacer(modifier = Modifier.width(SpaceSmall))


            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
fun PaymentStatusChip(
    modifier: Modifier = Modifier,
    paymentStatus : PaymentStatus,
    paidColor: Color = MaterialTheme.colorScheme.primary,
    notPaidColor: Color = MaterialTheme.colorScheme.secondary,
    absentColor: Color = MaterialTheme.colorScheme.error,
) {
    val bgColor = when(paymentStatus) {
        PaymentStatus.Absent -> absentColor
        PaymentStatus.NotPaid -> notPaidColor
        PaymentStatus.Paid -> paidColor
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(SpaceMini),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = paymentStatus.icon,
                contentDescription = paymentStatus.toString(),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(IconSizeSmall)
            )

            Spacer(modifier = Modifier.width(SpaceSmall))


            Text(
                text = paymentStatus.status,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}



@Composable
fun StandardChip(
    modifier: Modifier = Modifier,
    text: String = "",
    secondaryText: String = "",
    isPrimary: Boolean = false,
    isClickable: Boolean = false,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    secondaryColor: Color = MaterialTheme.colorScheme.secondary,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(if (isPrimary) primaryColor else secondaryColor)
            .clickable(isClickable){
                onClick()
            },
    ) {
        Row(
            modifier = Modifier
                .padding(SpaceMini),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if(!isPrimary){
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "$text added",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(IconSizeSmall)
                )

                Spacer(modifier = Modifier.width(SpaceSmall))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary,
            )

            if(secondaryText.isNotEmpty() && text.startsWith("Cold")){
                Text(
                    text = " Rs. $secondaryText",
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = if (isPrimary) secondaryColor else primaryColor,
                )
            }
        }
    }
}