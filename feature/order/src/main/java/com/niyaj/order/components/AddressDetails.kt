package com.niyaj.order.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.LightColor3
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Address
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardExpandable

/**
 * This composable displays the address details
 */
@Composable
fun AddressDetails(
    modifier: Modifier = Modifier,
    address: Address,
    doesExpanded: Boolean,
    onExpandChanged: () -> Unit,
    onClickViewDetails: (Int) -> Unit,
) = trace("AddressDetails") {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
            .clickable {
                onExpandChanged()
            },
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = LightColor3,
        ),
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
                    text = "Address Details",
                    icon = PoposIcons.LocationOn,
                    isTitle = true,
                )
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    onClick = {
                        onClickViewDetails(address.addressId)
                    },
                ) {
                    Icon(
                        imageVector = PoposIcons.OpenInNew,
                        contentDescription = "View Address Details",
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                }

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
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall),
                ) {
                    IconWithText(
                        text = "Short Name: ${address.shortName}",
                        icon = PoposIcons.Address,
                    )
                    Spacer(modifier = Modifier.height(SpaceSmall))

                    IconWithText(
                        text = "Name: ${address.addressName}",
                        icon = PoposIcons.Home,
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    IconWithText(
                        text = "Created At : ${address.createdAt.toPrettyDate()}",
                        icon = PoposIcons.AccessTime,
                    )

                    address.updatedAt?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        IconWithText(
                            text = "Updated At : ${it.toPrettyDate()}",
                            icon = PoposIcons.Update,
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceMedium))

                    StandardButton(
                        onClick = {
                            onClickViewDetails(address.addressId)
                        },
                        text = "View Address Details".uppercase(),
                        icon = PoposIcons.Details,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.outline,
                        ),
                    )
                }
            },
        )
    }
}
