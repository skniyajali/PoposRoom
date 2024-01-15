package com.niyaj.order.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreTime
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.designsystem.theme.LightColor2
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Customer
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardExpandable

/**
 * This composable displays the customer details
 */
@Composable
fun CustomerDetails(
    modifier: Modifier = Modifier,
    customer: Customer,
    doesExpanded : Boolean,
    onExpandChanged : () -> Unit,
    onClickViewDetails : (Int) -> Unit
) = trace("CustomerDetails") {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
            .clickable {
                onExpandChanged()
            },
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = LightColor2,
        )
    ) {
        StandardExpandable(
            onExpandChanged = {
                onExpandChanged()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = doesExpanded,
            title = {
                IconWithText(
                    text = "Customer Details",
                    icon = Icons.Default.Person,
                    isTitle = true
                )
            },
            rowClickable = true,
            expand = {  modifier: Modifier ->
                IconButton(
                    onClick = {
                        onClickViewDetails(customer.customerId)
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "View Address Details",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall),
                ) {
                    customer.customerName?.let {
                        IconWithText(
                            text = "Name: $it",
                            icon = Icons.Default.Person,
                        )
                        Spacer(modifier = Modifier.height(SpaceSmall))
                    }

                    IconWithText(
                        text = "Phone: ${customer.customerPhone}",
                        icon = Icons.Default.PhoneAndroid,
                    )

                    customer.customerEmail?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        IconWithText(
                            text = "Name: $it",
                            icon = Icons.Default.AlternateEmail,
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    IconWithText(
                        text = "Created At : ${customer.createdAt.toPrettyDate()}",
                        icon = Icons.Default.MoreTime
                    )

                    customer.updatedAt?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        IconWithText(
                            text = "Updated At : ${it.toPrettyDate()}",
                            icon = Icons.Default.Update
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceMedium))

                    StandardButton(
                        onClick = {
                            onClickViewDetails(customer.customerId)
                        },
                        text = "View Customer Details".uppercase(),
                        icon = Icons.Default.OpenInBrowser,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    )
                }
            },
        )
    }
}